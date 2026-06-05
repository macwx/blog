<template>
  <v-dialog v-model="forgetFlag" :fullscreen="isMobile" max-width="460">
    <v-card class="login-container" style="border-radius:4px">
      <v-icon class="float-right" @click="forgetFlag = false">
        mdi-close
      </v-icon>
      <div class="login-wrapper">
        <!-- 用户名 -->
        <v-text-field
          v-model="username"
          label="邮箱号"
          placeholder="请输入您的邮箱号"
          clearable
          @keyup.enter="forget"
        />
        <!-- 验证码 -->
        <div class="mt-7 send-wrapper">
          <v-text-field
            maxlength="6"
            v-model="code"
            label="验证码"
            placeholder="请输入6位验证码"
            @keyup.enter="forget"
          />
          <v-btn :disabled="flag" text small @click="sendCode">
            {{ codeMsg }}
          </v-btn>
        </div>
        <!-- 密码 -->
        <v-text-field
          v-model="password"
          class="mt-7"
          label="密码"
          placeholder="请输入您的密码"
          @keyup.enter="forget"
          :append-icon="show ? 'mdi-eye' : 'mdi-eye-off'"
          :type="show ? 'text' : 'password'"
          @click:append="show = !show"
        />
        <!-- 按钮 -->
        <v-btn
          class="mt-7"
          block
          color="green"
          style="color:#fff"
          @click="forget"
        >
          确定
        </v-btn>
        <!-- 登录 -->
        <div class="mt-10 login-tip">
          已有账号？<span @click="openLogin">登录</span>
        </div>
      </div>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { ref, computed, watch } from "vue";
import axios from "axios";
import { useBlogStore } from "@/store";
import { useToast } from "@/components/toast/useToast";
import config from "@/assets/js/config";

const store = useBlogStore();
const { toast } = useToast();

const username = ref("");
const code = ref("");
const password = ref("");
const flag = ref(true);
const codeMsg = ref("发送");
const time = ref(60);
const show = ref(false);
let timer = null;

const forgetFlag = computed({
  get() {
    return store.forgetFlag;
  },
  set(value) {
    store.forgetFlag = value;
  },
});

const isMobile = computed(() => {
  const clientWidth = document.documentElement.clientWidth;
  return clientWidth <= 960;
});

watch(username, (value) => {
  var reg = /^[A-Za-z0-9一-龥]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
  flag.value = !reg.test(value);
});

function openLogin() {
  store.forgetFlag = false;
  store.loginFlag = true;
}

function sendCode() {
  // eslint-disable-next-line no-undef
  var captcha = new TencentCaptcha(config.TENCENT_CAPTCHA, function (res) {
    if (res.ret === 0) {
      //发送邮件
      countDown();
      axios
        .get("/api/users/code", {
          params: { username: username.value },
        })
        .then(({ data }) => {
          if (data.flag) {
            toast({ type: "success", message: "发送成功" });
          } else {
            toast({ type: "error", message: data.message });
          }
        });
    }
  });
  // 显示验证码
  captcha.show();
}

function countDown() {
  flag.value = true;
  timer = setInterval(() => {
    time.value--;
    codeMsg.value = time.value + "s";
    if (time.value <= 0) {
      clearInterval(timer);
      codeMsg.value = "发送";
      time.value = 60;
      flag.value = false;
    }
  }, 1000);
}

function forget() {
  var reg = /^[A-Za-z0-9一-龥]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
  if (!reg.test(username.value)) {
    toast({ type: "error", message: "邮箱格式不正确" });
    return false;
  }
  if (code.value.trim().length != 6) {
    toast({ type: "error", message: "请输入6位验证码" });
    return false;
  }
  if (password.value.trim().length < 6) {
    toast({ type: "error", message: "密码不能少于6位" });
    return false;
  }
  const user = {
    username: username.value,
    password: password.value,
    code: code.value,
  };
  axios.put("/api/users/password", user).then(({ data }) => {
    if (data.flag) {
      toast({ type: "success", message: "修改成功" });
    } else {
      toast({ type: "error", message: data.message });
    }
  });
}
</script>
