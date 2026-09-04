<template>
  <v-dialog v-model="emailFlag" :fullscreen="isMobile" max-width="460">
    <v-card class="login-container" style="border-radius:4px">
      <v-icon class="float-right" @click="emailFlag = false">
        mdi-close
      </v-icon>
      <div class="login-wrapper">
        <!-- 用户名 -->
        <v-text-field
          v-model="email"
          label="邮箱号"
          placeholder="请输入您的邮箱号"
          clearable
          @keyup.enter="register"
        />
        <!-- 验证码 -->
        <div class="mt-7 send-wrapper">
          <v-text-field
            maxlength="6"
            v-model="code"
            label="验证码"
            placeholder="请输入6位验证码"
            @keyup.enter="register"
          />
          <v-btn text small :disabled="flag" @click="sendCode">
            {{ codeMsg }}
          </v-btn>
        </div>
        <!-- 按钮 -->
        <v-btn
          class="mt-7"
          block
          color="blue"
          style="color:#fff"
          @click="saveUserEmail"
        >
          绑定
        </v-btn>
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
import { loadTencentCaptcha } from "@/utils/thirdPartySdk";

const store = useBlogStore();
const { toast } = useToast();

const email = ref(store.email);
const code = ref("");
const flag = ref(true);
const codeMsg = ref("发送");
const time = ref(60);
const show = ref(false);
let timer = null;

const emailFlag = computed({
  get() {
    return store.emailFlag;
  },
  set(value) {
    store.emailFlag = value;
  },
});

const isMobile = computed(() => {
  const clientWidth = document.documentElement.clientWidth;
  return clientWidth <= 960;
});

watch(email, (value) => {
  var reg = /^[A-Za-z0-9一-龥]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
  flag.value = !reg.test(value);
});

function sendCode() {
  loadTencentCaptcha().then(() => {
    var captcha = new TencentCaptcha(config.TENCENT_CAPTCHA, function (res) {
      if (res.ret === 0) {
        //发送邮件
        countDown();
        axios
          .get("/api/users/code", {
            params: { username: email.value },
          })
          .then(({ data }) => {
            if (data.flag) {
              toast({ type: "success", message: data.message });
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

function saveUserEmail() {
  var reg = /^[A-Za-z0-9一-龥]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
  if (!reg.test(email.value)) {
    toast({ type: "error", message: "邮箱格式不正确" });
    return false;
  }
  if (code.value.trim().length != 6) {
    toast({ type: "error", message: "请输入6位验证码" });
    return false;
  }
  const user = {
    email: email.value,
    code: code.value,
  };
  axios.post("/api/users/email", user).then(({ data }) => {
    if (data.flag) {
      store.saveEmail(email.value);
      email.value = "";
      code.value = "";
      store.closeModel();
      toast({ type: "success", message: data.message });
    } else {
      toast({ type: "error", message: data.message });
    }
  });
}
</script>

<style scoped>
@media (min-width: 760px) {
  .login-container {
    padding: 1rem;
    border-radius: 4px;
    height: 400px;
  }
}
</style>
