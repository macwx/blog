import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import vuetify from "vite-plugin-vuetify";
import { fileURLToPath, URL } from "node:url";

export default defineConfig({
  plugins: [
    vue(),
    vuetify({ autoImport: true }),
  ],
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
    },
  },
  server: {
    port: 8080,
    proxy: {
      "/api": {
        target: "http://62.234.32.203:8080/",
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ""),
      },
      "/clawdchat-api": {
        target: "https://clawdchat.cn",
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/clawdchat-api/, ""),
      },
    },
  },
  build: {
    target: "es2020",
    sourcemap: false,
    chunkSizeWarningLimit: 1000,
    rollupOptions: {
      output: {
        manualChunks: {
          vuetify: ["vuetify"],
          vendor: ["vue", "vue-router", "pinia", "axios"],
          markdown: [
            "markdown-it",
            "markdown-it-abbr",
            "markdown-it-container",
            "markdown-it-deflist",
            "markdown-it-emoji",
            "markdown-it-footnote",
            "markdown-it-ins",
            "markdown-it-katex-external",
            "markdown-it-mark",
            "markdown-it-sub",
            "markdown-it-sup",
            "markdown-it-task-lists",
          ],
          highlight: ["highlight.js"],
          dayjs: ["dayjs"],
        },
      },
    },
  },
});
