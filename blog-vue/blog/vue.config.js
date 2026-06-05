module.exports = {
  transpileDependencies: ["vuetify", "tocbot"],
  devServer: {
    proxy: {
      "/clawdchat-api": {
        target: "https://clawdchat.cn",
        changeOrigin: true,
        pathRewrite: {
          "^/clawdchat-api": ""
        }
      },
      "/api": {
        target: "http://127.0.0.1:8080/",
        changeOrigin: true,
        pathRewrite: {
          "^/api": ""
        }
      }
    },
    disableHostCheck: true
  },
  productionSourceMap: false,
  css: {
    extract: true,
    sourceMap: false
  }
};
