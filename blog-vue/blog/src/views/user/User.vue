<template>
  <div>
    <div class="banner" :style="cover">
      <h1 class="banner-title">个人中心</h1>
    </div>
    <v-card class="blog-container">
      <div>
        <span class="info-title">基本信息</span>
      </div>
      <v-row class="info-wrapper">
        <v-col md="3" cols="12">
          <button id="pick-avatar" @click="triggerFileInput">
            <v-avatar size="140">
              <img :src="store.avatar" />
            </v-avatar>
          </button>
          <!-- TODO: Vue3 migration - vue-avatar-cropper removed, using simple file input -->
          <input
            ref="fileInput"
            type="file"
            accept="image/*"
            style="display: none"
            @change="uploadAvatar"
          />
        </v-col>
        <v-col md="7" cols="12">
          <v-text-field
            v-model="userInfo.nickname"
            label="昵称"
            placeholder="请输入您的昵称"
          />
          <v-text-field
            v-model="userInfo.webSite"
            class="mt-7"
            label="个人网站"
            placeholder="http://你的网址"
          />
          <v-text-field
            v-model="userInfo.intro"
            class="mt-7"
            label="简介"
            placeholder="介绍下自己吧"
          />
          <div v-if="loginType != 0" class="mt-7 binding">
            <v-text-field
              disabled
              v-model="email"
              label="邮箱号"
              placeholder="请绑定邮箱"
            />
            <v-btn v-if="email" variant="text" size="small" @click="openEmailModel">
              修改绑定
            </v-btn>
            <v-btn v-else variant="text" size="small" @click="openEmailModel">
              绑定邮箱
            </v-btn>
          </div>
          <v-btn @click="updataUserInfo" variant="outlined" class="mt-5">修改</v-btn>
        </v-col>
      </v-row>
    </v-card>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import axios from "axios";
import { useBlogStore } from "@/store";
import { useToast } from "@/components/toast/useToast";

const store = useBlogStore();
const toast = useToast();

const fileInput = ref(null);

const userInfo = ref({
  nickname: store.nickname,
  intro: store.intro,
  webSite: store.webSite,
  loginType: store.loginType
});

function updataUserInfo() {
  axios.put("/api/users/info", userInfo.value).then(({ data }) => {
    if (data.flag) {
      store.updateUserInfo(userInfo.value);
      toast({ type: "success", message: "修改成功" });
    } else {
      toast({ type: "error", message: data.message });
    }
  });
}

function triggerFileInput() {
  if (fileInput.value) {
    fileInput.value.click();
  }
}

function uploadAvatar(event) {
  const file = event.target.files[0];
  if (!file) return;
  const formData = new FormData();
  formData.append("file", file);
  axios
    .post("/api/users/avatar", formData, {
      headers: { "Content-Type": "multipart/form-data" }
    })
    .then(({ data }) => {
      if (data.flag) {
        store.updateAvatar(data.data);
        toast({ type: "success", message: "上传成功" });
      } else {
        toast({ type: "error", message: data.message });
      }
    });
}

function openEmailModel() {
  store.emailFlag = true;
}

const email = computed(() => {
  return store.email;
});

const loginType = computed(() => {
  return store.loginType;
});

const cover = computed(() => {
  var c = "";
  store.blogInfo.pageList.forEach(item => {
    if (item.pageLabel == "user") {
      c = item.pageCover;
    }
  });
  return "background: url(" + c + ") center center / cover no-repeat";
});
</script>

<style scoped>
.info-title {
  font-size: 1.25rem;
  font-weight: bold;
}
.info-wrapper {
  margin-top: 1rem;
  display: flex;
  align-items: center;
  justify-content: center;
}
#pick-avatar {
  outline: none;
}
.binding {
  display: flex;
  align-items: center;
}
</style>
