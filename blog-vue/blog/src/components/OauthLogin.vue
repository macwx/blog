<template>
  <div class="oauth-background">
    <div id="preloader_1">
      <span></span>
      <span></span>
      <span></span>
      <span></span>
      <span></span>
    </div>
  </div>
</template>

<script setup>
/* eslint-disable no-undef */
import { onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import axios from "axios";
import { useBlogStore } from "@/store";
import { useToast } from "@/components/toast/useToast";

const store = useBlogStore();
const router = useRouter();
const route = useRoute();
const toast = useToast();

onMounted(() => {
  //关闭登录框
  store.loginFlag = false;
  //通过路径判断是微博登录还是qq登录
  if (route.path == "/oauth/login/qq") {
    // 拿到openId，accessToken传入后台
    if (QC.Login.check()) {
      QC.Login.getMe(function(openId, accessToken) {
        axios
          .post("/api/users/oauth/qq", {
            openId: openId,
            accessToken: accessToken
          })
          .then(({ data }) => {
            if (data.flag) {
              store.login(data.data);
              if (data.data.email == null) {
                toast({
                  type: "warnning",
                  message: "请绑定邮箱以便及时收到回复"
                });
              } else {
                toast({ type: "success", message: data.message });
              }
            } else {
              toast({ type: "error", message: data.message });
            }
          });
      });
    } else {
      toast({ type: "error", message: "QQ登录验证失败" });
    }
  } else {
    axios
      .post("/api/users/oauth/weibo", { code: route.query.code })
      .then(({ data }) => {
        if (data.flag) {
          store.login(data.data);
          if (data.data.email == null) {
            toast({
              type: "warnning",
              message: "请绑定邮箱以便及时收到回复"
            });
          } else {
            toast({ type: "success", message: data.message });
          }
        } else {
          toast({ type: "error", message: data.message });
        }
      });
  }
  // 跳转回原页面
  const loginUrl = store.loginUrl;
  if (loginUrl != null && loginUrl != "") {
    router.push({ path: loginUrl });
  } else {
    router.push({ path: "/" });
  }
});
</script>

<style scoped>
.oauth-background {
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  z-index: 1000;
}
#preloader_1 {
  position: relative;
  top: 45vh;
  left: 45vw;
}
#preloader_1 span {
  display: block;
  bottom: 0px;
  width: 9px;
  height: 5px;
  background: #9b59b6;
  position: absolute;
  animation: preloader_1 1.5s infinite ease-in-out;
}
#preloader_1 span:nth-child(2) {
  left: 11px;
  animation-delay: 0.2s;
}
#preloader_1 span:nth-child(3) {
  left: 22px;
  animation-delay: 0.4s;
}
#preloader_1 span:nth-child(4) {
  left: 33px;
  animation-delay: 0.6s;
}
#preloader_1 span:nth-child(5) {
  left: 44px;
  animation-delay: 0.8s;
}
@keyframes preloader_1 {
  0% {
    height: 5px;
    transform: translateY(0px);
    background: #9b59b6;
  }
  25% {
    height: 30px;
    transform: translateY(15px);
    background: #3498db;
  }
  50% {
    height: 5px;
    transform: translateY(0px);
    background: #9b59b6;
  }
  100% {
    height: 5px;
    transform: translateY(0px);
    background: #9b59b6;
  }
}
</style>
