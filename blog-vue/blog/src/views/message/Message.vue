<template>
  <div>
    <!-- banner -->
    <div class="message-banner" :style="cover">
      <!-- 弹幕输入框 -->
      <div class="message-container">
        <h1 class="message-title">留言板</h1>
        <div class="animated fadeInUp message-input-wrapper">
          <input
            v-model="messageContent"
            @click="show = true"
            @keyup.enter="addToList"
            placeholder="说点什么吧"
          />
          <button
            class="ml-3 animated bounceInLeft"
            @click="addToList"
            v-show="show"
          >
            发送
          </button>
        </div>
      </div>
      <!-- 弹幕列表 -->
      <div class="barrage-container">
        <div
          v-for="(item, index) in barrageList"
          :key="index"
          class="barrage-items"
          :style="barrageStyle(index, item)"
        >
          <img
            :src="item.avatar"
            width="30"
            height="30"
            style="border-radius:50%"
          />
          <span class="ml-2">{{ item.nickname }} :</span>
          <span class="ml-2 barrage-text">{{ item.messageContent }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import axios from "axios";
import { useBlogStore } from "@/store";
import { useToast } from "@/components/toast/useToast";

const store = useBlogStore();
const toast = useToast();

const show = ref(false);
const messageContent = ref("");
const barrageList = ref([]);

function addToList() {
  if (messageContent.value.trim() == "") {
    toast({ type: "error", message: "留言不能为空" });
    return false;
  }
  const userAvatar = store.avatar
    ? store.avatar
    : store.blogInfo.websiteConfig.touristAvatar;
  const userNickname = store.nickname
    ? store.nickname
    : "游客";
  var message = {
    avatar: userAvatar,
    nickname: userNickname,
    messageContent: messageContent.value,
    time: Math.floor(Math.random() * (10 - 7)) + 7
  };
  messageContent.value = "";
  axios.post("/api/messages", message).then(({ data }) => {
    if (data.flag) {
      barrageList.value.push(message);
      toast({ type: "success", message: "留言成功" });
    } else {
      toast({ type: "error", message: data.message });
    }
  });
}

function listMessage() {
  axios.get("/api/messages").then(({ data }) => {
    if (data.flag) {
      barrageList.value = data.data;
    }
  });
}

const cover = computed(() => {
  var c = "";
  store.blogInfo.pageList.forEach(item => {
    if (item.pageLabel == "message") {
      c = item.pageCover;
    }
  });
  return "background: url(" + c + ") center center / cover no-repeat";
});

// 确定性伪随机：同一条留言每次渲染位置、速度保持一致，避免跳动
function pseudoRandom(seed) {
  const x = Math.sin(seed * 999) * 10000;
  return x - Math.floor(x);
}

// 10 条泳道循环使用；keyframes 里飞行段只占 8.5%，其余时间停在屏幕外，
// 保证 123 条留言同时只有 ~10 条在屏上（与原 vue-baberrage 的泳道效果一致）
function barrageStyle(index, item) {
  const lane = index % 10;
  const top = 4 + lane * 9 + pseudoRandom(index + 1) * 3;
  const duration = item.time || Math.floor(pseudoRandom(index + 101) * 3) + 7;
  const delay = (index % 12) * 1.1 + pseudoRandom(index + 201) * 2;
  return {
    top: top + "%",
    animationDuration: duration * 12 + "s",
    animationDelay: delay + "s"
  };
}

onMounted(() => {
  listMessage();
});
</script>

<style scoped>
.message-banner {
  position: absolute;
  top: -60px;
  left: 0;
  right: 0;
  height: 100vh;
  background-color: #49b1f5;
  animation: header-effect 1s;
}
.message-title {
  color: #eee;
  animation: title-scale 1s;
}
.message-container {
  position: absolute;
  width: 360px;
  top: 35%;
  left: 0;
  right: 0;
  text-align: center;
  z-index: 5;
  margin: 0 auto;
  color: #fff;
}
.message-input-wrapper {
  display: flex;
  justify-content: center;
  height: 2.5rem;
  margin-top: 2rem;
}
.message-input-wrapper input {
  outline: none;
  width: 70%;
  border-radius: 20px;
  height: 100%;
  padding: 0 1.25rem;
  color: #eee;
  border: #fff 1px solid;
}
.message-input-wrapper input::-webkit-input-placeholder {
  color: #eeee;
}
.message-input-wrapper button {
  outline: none;
  border-radius: 20px;
  height: 100%;
  padding: 0 1.25rem;
  border: #fff 1px solid;
}
.barrage-container {
  position: absolute;
  top: 50px;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
}
.barrage-items {
  position: absolute;
  left: 100%;
  max-width: 26em;
  white-space: nowrap;
  background: rgb(0, 0, 0, 0.7);
  border-radius: 100px;
  color: #fff;
  padding: 5px 10px 5px 5px;
  align-items: center;
  display: flex;
  animation-name: barrage-move;
  animation-timing-function: linear;
  animation-iteration-count: infinite;
  will-change: transform;
}
.barrage-text {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
@keyframes barrage-move {
  0% {
    transform: translateX(0);
  }
  8.5% {
    transform: translateX(calc(-100vw - 100%));
  }
  100% {
    transform: translateX(calc(-100vw - 100%));
  }
}
</style>
