<template>
  <v-dialog v-model="loginFlag" :fullscreen="isMobile" max-width="460">
    <v-card class="login-container" style="border-radius:4px">
      <v-icon class="float-right" @click="loginFlag = false">
        mdi-close
      </v-icon>
      <div class="login-wrapper">
        <!-- 用户名 -->
        <v-text-field
          v-model="username"
          label="邮箱号"
          placeholder="请输入您的邮箱号"
          clearable
          @keyup.enter="login"
        />
        <!-- 密码 -->
        <v-text-field
          v-model="password"
          class="mt-7"
          label="密码"
          placeholder="请输入您的密码"
          @keyup.enter="login"
          :append-icon="show ? 'mdi-eye' : 'mdi-eye-off'"
          :type="show ? 'text' : 'password'"
          @click:append="show = !show"
        />
        <!-- 按钮 -->
        <v-btn
          class="mt-7"
          block
          color="blue"
          style="color:#fff"
          @click="login"
        >
          登录
        </v-btn>
        <!-- 注册和找回密码 -->
        <div class="mt-10 login-tip">
          <span @click="openRegister">立即注册</span>
          <span @click="openForget" class="float-right">忘记密码?</span>
        </div>
        <div v-if="socialLoginList.length > 0">
          <div class="social-login-title">社交账号登录</div>
          <div class="social-login-wrapper">
            <!-- 微博登录 -->
            <a
              v-if="showLogin('weibo')"
              class="mr-3 iconfont iconweibo"
              style="color:#e05244"
              @click="weiboLogin"
            />
            <!-- qq登录 -->
            <a
              v-if="showLogin('qq')"
              class="iconfont iconqq"
              style="color:#00AAEE"
              @click="qqLogin"
            />
          </div>
        </div>
      </div>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { ref, computed } from "vue";
import { useRouter, useRoute } from "vue-router";
import axios from "axios";
import { useBlogStore } from "@/store";
import { useToast } from "@/components/toast/useToast";
import config from "@/assets/js/config";
import { loadQQSdk, loadTencentCaptcha } from "@/utils/thirdPartySdk";

const store = useBlogStore();
const router = useRouter();
const route = useRoute();
const { toast } = useToast();

const username = ref("");
const password = ref("");
const show = ref(false);

const loginFlag = computed({
  get() {
    return store.loginFlag;
  },
  set(value) {
    store.loginFlag = value;
  },
});

const isMobile = computed(() => {
  const clientWidth = document.documentElement.clientWidth;
  return clientWidth <= 960;
});

const socialLoginList = computed(() => {
  return store.blogInfo.websiteConfig.socialLoginList || [];
});

function showLogin(type) {
  return socialLoginList.value && socialLoginList.value.indexOf(type) !== -1;
}

function openRegister() {
  store.loginFlag = false;
  store.registerFlag = true;
}

function openForget() {
  store.loginFlag = false;
  store.forgetFlag = true;
}

function login() {
  var reg = /^[A-Za-z0-9一-龥]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
  if (!reg.test(username.value)) {
    toast({ type: "error", message: "邮箱格式不正确" });
    return false;
  }
  if (password.value.trim().length == 0) {
    toast({ type: "error", message: "密码不能为空" });
    return false;
  }
  loadTencentCaptcha().then(() => {
    var captcha = new TencentCaptcha(config.TENCENT_CAPTCHA, function (res) {
      if (res.ret === 0) {
        //发送登录请求
        let param = new URLSearchParams();
        param.append("username", username.value);
        param.append("password", password.value);
        axios.post("/api/login", param).then(({ data }) => {
          if (data.flag) {
            username.value = "";
            password.value = "";
            store.login(data.data);
            store.closeModel();
            toast({ type: "success", message: "登录成功" });
          } else {
            toast({ type: "error", message: data.message });
          }
        });
      }
    });
    // 显示验证码
    captcha.show();
  });
}

function qqLogin() {
  //保留当前路径
  store.saveLoginUrl(route.path);
  if (
    navigator.userAgent.match(
      /(iPhone|iPod|Android|ios|iOS|iPad|Backerry|WebOS|Symbian|Windows Phone|Phone)/i
    )
  ) {
    loadQQSdk().then(() => {
      // eslint-disable-next-line no-undef
      QC.Login.showPopup({
        appId: config.QQ_APP_ID,
        redirectURI: config.QQ_REDIRECT_URI,
      });
    });
  } else {
    window.open(
      "https://graph.qq.com/oauth2.0/show?which=Login&display=pc&client_id=" +
        +config.QQ_APP_ID +
        "&response_type=token&scope=all&redirect_uri=" +
        config.QQ_REDIRECT_URI,
      "_self"
    );
  }
}

function weiboLogin() {
  //保留当前路径
  store.saveLoginUrl(route.path);
  window.open(
    "https://api.weibo.com/oauth2/authorize?client_id=" +
      config.WEIBO_APP_ID +
      "&response_type=code&redirect_uri=" +
      config.WEIBO_REDIRECT_URI,
    "_self"
  );
}
</script>

<style scoped>
.social-login-title {
  margin-top: 1.5rem;
  color: #b5b5b5;
  font-size: 0.75rem;
  text-align: center;
}
.social-login-title::before {
  content: "";
  display: inline-block;
  background-color: #d8d8d8;
  width: 60px;
  height: 1px;
  margin: 0 12px;
  vertical-align: middle;
}
.social-login-title::after {
  content: "";
  display: inline-block;
  background-color: #d8d8d8;
  width: 60px;
  height: 1px;
  margin: 0 12px;
  vertical-align: middle;
}
.social-login-wrapper {
  margin-top: 1rem;
  font-size: 2rem;
  text-align: center;
}
.social-login-wrapper a {
  text-decoration: none;
}
</style>
