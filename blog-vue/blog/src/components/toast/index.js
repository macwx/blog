import { h, render } from "vue";
import ToastComponent from "./Toast.vue";
import { useToast } from "./useToast";

const ToastPlugin = {
  install(app) {
    const mountNode = document.createElement("div");
    document.body.appendChild(mountNode);
    const vnode = h(ToastComponent);
    vnode.appContext = app._context;
    render(vnode, mountNode);

    const { toast } = useToast();
    app.config.globalProperties.$toast = toast;
  },
};

export default ToastPlugin;
