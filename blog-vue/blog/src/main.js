import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import { createPinia } from "pinia";
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";
import vuetify from "./plugins/vuetify";
import axios from "axios";
import dayjs from "dayjs";
import NProgress from "nprogress";
import "nprogress/nprogress.css";
import "./assets/css/animate-lite.css";
import "./assets/css/index.css";
import "./assets/css/iconfont.css";
import "./assets/css/markdown.css";
import "./assets/css/vue-social-share/client.css";
import "highlight.js/styles/atom-one-dark.min.css";

const app = createApp(App);

// Pinia
const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);
app.use(pinia);

// Router
app.use(router);

// Vuetify
app.use(vuetify);

// Global properties
app.config.globalProperties.$axios = axios;
app.config.globalProperties.$dayjs = dayjs;

// Global filters as functions (Vue 3 removed filters)
app.config.globalProperties.$filters = {
  date(value) {
    return dayjs(value).format("YYYY-MM-DD");
  },
  year(value) {
    return dayjs(value).format("YYYY");
  },
  hour(value) {
    return dayjs(value).format("HH:mm:ss");
  },
  time(value) {
    return dayjs(value).format("YYYY-MM-DD HH:mm:ss");
  },
  num(value) {
    if (value >= 1000) {
      return (value / 1000).toFixed(1) + "k";
    }
    return value;
  },
};

// Toast plugin
import Toast from "./components/toast/index";
app.use(Toast);

// NProgress
router.beforeEach((to, from, next) => {
  NProgress.start();
  if (to.meta.title) {
    document.title = to.meta.title;
  }
  next();
});

router.afterEach(() => {
  window.scrollTo({ top: 0, behavior: "instant" });
  NProgress.done();
});

// Axios interceptors
axios.interceptors.response.use(
  (response) => {
    switch (response.data.code) {
      case 50000:
        app.config.globalProperties.$toast({ type: "error", message: "系统异常" });
    }
    return response;
  },
  (error) => Promise.reject(error)
);

app.mount("#app");
