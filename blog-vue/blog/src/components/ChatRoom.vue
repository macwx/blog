<template>
  <div>
    <!-- 聊天界面 -->
    <div
      class="chat-container animated bounceInUp"
      v-show="isShow"
      @click="closeAll"
      @contextmenu.prevent.stop="closeAll"
    >
      <!-- 标题 -->
      <div class="header">
        <img
          width="32"
          height="32"
          src="https://macwoss.oss-cn-beijing.aliyuncs.com/articles/13d397a33d8cacee06ee644e6d540c2d.png"
        />
        <div style="margin-left:12px">
          <div>聊天室</div>
          <div style="font-size:12px">当前{{ count }}人在线</div>
        </div>
        <v-icon class="close" @click="isShow = false">
          mdi-close
        </v-icon>
      </div>
      <!-- 对话内容 -->
      <div class="message" id="message">
        <!-- 录音遮罩层 -->
        <div
          v-show="voiceActive"
          class="voice"
          @mousemove.prevent.stop="translationmove($event)"
          @mouseup.prevent.stop="translationEnd($event)"
        >
          <v-icon ref="voiceCloseRef" class="close-voice">mdi-close</v-icon>
        </div>
        <div
          :class="isMyMessage(item)"
          v-for="(item, index) of chatRecordList"
          :key="index"
        >
          <!-- 头像 -->
          <img :src="item.avatar" :class="isleft(item)" />
          <div>
            <div class="nickname" v-if="!isSelf(item)">
              {{ item.nickname }}
              <span style="margin-left:12px">{{ formatHour(item.createTime) }}</span>
            </div>
            <!-- 内容 -->
            <div
              :ref="el => { if (el) contentRefs[index] = el }"
              @contextmenu.prevent.stop="showBack(item, index, $event)"
              :class="isMyContent(item)"
            >
              <!-- 文字消息 -->
              <div v-if="item.type == 3" v-html="item.content" />
              <!-- 语音消息 -->
              <div v-if="item.type == 5" @click.prevent.stop="playVoice(item)">
                <audio
                  @ended="endVoice(item)"
                  @canplay="getVoiceTime(item)"
                  :ref="el => { if (el) voicesRefs[item.id] = el }"
                  :src="item.content"
                  style="display:none"
                />
                <!-- 播放 -->
                <v-icon
                  :color="isSelf(item) ? '#fff' : '#000'"
                  :ref="el => { if (el) playsRefs[item.id] = el }"
                  style="display:inline-flex;cursor: pointer;"
                >
                  mdi-arrow-right-drop-circle
                </v-icon>
                <!-- 暂停 -->
                <v-icon
                  :color="isSelf(item) ? '#fff' : '#000'"
                  :ref="el => { if (el) pausesRefs[item.id] = el }"
                  style="display:none;cursor: pointer;"
                >
                  mdi-pause-circle
                </v-icon>
                <!-- 音频时长 -->
                <span :ref="el => { if (el) voiceTimesRefs[item.id] = el }" />
              </div>
              <div
                class="back-menu"
                :ref="el => { if (el) backBtnRefs[index] = el }"
                @click="back(item, index)"
              >
                撤回
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- 输入框 -->
      <div class="footer">
        <!-- 表情框 -->
        <div class="emoji-box" v-show="isEmoji">
          <Emoji :chooseEmoji="true" @addEmoji="addEmoji" />
        </div>
        <div class="emoji-border" v-show="isEmoji" />
        <!-- 切换输入方式 -->
        <v-icon
          v-show="!isVoice"
          @click="isVoice = !isVoice"
          style="margin-right: 8px"
        >
          mdi-microphone
        </v-icon>
        <v-icon
          v-show="isVoice"
          @click="isVoice = !isVoice"
          style="margin-right: 8px"
        >
          mdi-keyboard
        </v-icon>
        <!-- 文字输入 -->
        <textarea
          v-show="!isVoice"
          ref="chatInputRef"
          v-model="content"
          @keydown.enter="saveMessage($event)"
          placeholder="请输入内容"
        />
        <!-- 语音输入 -->
        <button
          class="voice-btn"
          v-show="isVoice"
          @mousedown.prevent.stop="translationStart"
          @mouseup.prevent.stop="translationEnd($event)"
          @touchstart.prevent.stop="translationStart"
          @touchend.prevent.stop="translationEnd($event)"
          @touchmove.prevent.stop="translationmove($event)"
        >
          按住说话
        </button>
        <!-- 表情 -->
        <i
          class="iconfont iconbiaoqing emoji"
          :style="isEmoji ? 'color:#FFC83D' : ''"
          @click.prevent.stop="openEmoji"
        />
        <!-- 发送按钮 -->
        <i :class="isInput" @click="saveMessage" style="font-size: 1.5rem" />
      </div>
    </div>
    <!-- 未读数量 -->
    <div class="chat-btn" @click="open">
      <span class="unread" v-if="unreadCount > 0">{{ unreadCount }}</span>
      <img
        width="100%"
        height="100%"
        src="https://macwoss.oss-cn-beijing.aliyuncs.com/articles/13d397a33d8cacee06ee644e6d540c2d.png"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUpdated, onBeforeUnmount, nextTick } from "vue";
import axios from "axios";
import { useBlogStore } from "@/store";
import { useToast } from "@/components/toast/useToast";
import Recorderx, { ENCODE_TYPE } from "recorderx";
import Emoji from "./Emoji.vue";
import EmojiList from "../assets/js/emoji";

const store = useBlogStore();
const toast = useToast();

// Refs for DOM elements
const chatInputRef = ref(null);
const voiceCloseRef = ref(null);
const voicesRefs = reactive({});
const playsRefs = reactive({});
const pausesRefs = reactive({});
const voiceTimesRefs = reactive({});
const backBtnRefs = reactive({});
const contentRefs = reactive({});

const isEmoji = ref(false);
const isShow = ref(false);
const websocket = ref(null);
const content = ref("");
const chatRecordList = ref([]);
const voiceList = ref([]);
let rc = null;
const ipAddress = ref("");
const ipSource = ref("");
const count = ref(0);
const unreadCount = ref(0);
const isVoice = ref(false);
const voiceActive = ref(false);
const startVoiceTime = ref(null);
const WebsocketMessage = reactive({
  type: null,
  data: null
});
let heartBeat = null;

function formatHour(time) {
  // TODO: Vue3 migration - replaced | hour filter
  return time;
}

function isSelf(item) {
  return (
    item.ipAddress == ipAddress.value ||
    (item.userId != null && item.userId == store.userId)
  );
}

function isleft(item) {
  return isSelf(item)
    ? "user-avatar right-avatar"
    : "user-avatar left-avatar";
}

function isMyContent(item) {
  return isSelf(item) ? "my-content" : "user-content";
}

function isMyMessage(item) {
  return isSelf(item) ? "my-message" : "user-message";
}

const blogInfo = computed(() => {
  return store.blogInfo;
});

const nickname = computed(() => {
  return store.nickname != null
    ? store.nickname
    : ipAddress.value;
});

const avatar = computed(() => {
  return store.avatar != null
    ? store.avatar
    : store.blogInfo.websiteConfig.touristAvatar;
});

const isInput = computed(() => {
  return content.value.trim() != ""
    ? "iconfont iconzhifeiji submit-btn"
    : "iconfont iconzhifeiji";
});

function open() {
  if (websocket.value == null) {
    connect();
  }
  unreadCount.value = 0;
  isShow.value = !isShow.value;
}

function openEmoji() {
  isEmoji.value = !isEmoji.value;
  isVoice.value = false;
}

function connect() {
  // 生成或获取客户端唯一标识
  var clientId = localStorage.getItem("chat_client_id");
  if (!clientId) {
    clientId = "cid_" + Math.random().toString(36).substr(2, 16) + "_" + Date.now();
    localStorage.setItem("chat_client_id", clientId);
  }
  websocket.value = new WebSocket(blogInfo.value.websiteConfig.websocketUrl + "?clientId=" + clientId);
  websocket.value.onerror = function(event) {
    console.log(event);
    alert("失败");
  };
  websocket.value.onopen = function(event) {
    console.log(event);
    heartBeat = setInterval(function() {
      var beatMessage = {
        type: 6,
        data: "ping"
      };
      websocket.value.send(JSON.stringify(beatMessage));
    }, 30 * 1000);
  };
  websocket.value.onmessage = function(event) {
    const data = JSON.parse(event.data);
    switch (data.type) {
      case 1:
        count.value = data.data;
        break;
      case 2:
        chatRecordList.value = data.data.chatRecordList;
        chatRecordList.value.forEach(item => {
          if (item.type == 5) {
            voiceList.value.push(item.id);
          }
        });
        ipAddress.value = data.data.ipAddress;
        ipSource.value = data.data.ipSource;
        break;
      case 3:
        chatRecordList.value.push(data.data);
        if (!isShow.value) {
          unreadCount.value++;
        }
        break;
      case 4:
        if (data.data.isVoice) {
          voiceList.value.splice(voiceList.value.indexOf(data.data.id), 1);
        }
        for (var i = 0; i < chatRecordList.value.length; i++) {
          if (chatRecordList.value[i].id == data.data.id) {
            chatRecordList.value.splice(i, 1);
            i--;
          }
        }
        break;
      case 5:
        voiceList.value.push(data.data.id);
        chatRecordList.value.push(data.data);
        if (!isShow.value) {
          unreadCount.value++;
        }
        break;
    }
  };
  websocket.value.onclose = function() {};
}

function saveMessage(e) {
  e.preventDefault();
  if (content.value.trim() == "") {
    toast({ type: "error", message: "内容不能为空" });
    return false;
  }
  var reg = /\[.+?\]/g;
  content.value = content.value.replace(reg, function(str) {
    return (
      "<img src= '" +
      EmojiList[str] +
      "' width='24'height='24' style='margin: 0 1px;vertical-align: text-bottom'/>"
    );
  });
  var socketMsg = {
    nickname: nickname.value,
    avatar: avatar.value,
    content: content.value,
    userId: store.userId,
    type: 3,
    ipAddress: ipAddress.value,
    ipSource: ipSource.value
  };
  WebsocketMessage.type = 3;
  WebsocketMessage.data = socketMsg;
  websocket.value.send(JSON.stringify(WebsocketMessage));
  content.value = "";
}

function addEmoji(key) {
  isEmoji.value = false;
  if (chatInputRef.value) {
    chatInputRef.value.focus();
  }
  content.value += key;
}

function showBack(item, index, e) {
  Object.values(backBtnRefs).forEach(el => {
    if (el) el.style.display = "none";
  });
  if (
    item.ipAddress == ipAddress.value ||
    (item.userId != null && item.userId == store.userId)
  ) {
    if (backBtnRefs[index]) {
      backBtnRefs[index].style.left = e.offsetX + "px";
      backBtnRefs[index].style.bottom = e.offsetY + "px";
      backBtnRefs[index].style.display = "block";
    }
  }
}

function back(item, index) {
  var socketMsg = {
    id: item.id,
    isVoice: item.type == 5
  };
  WebsocketMessage.type = 4;
  WebsocketMessage.data = socketMsg;
  websocket.value.send(JSON.stringify(WebsocketMessage));
  if (backBtnRefs[index]) {
    backBtnRefs[index].style.display = "none";
  }
}

function closeAll() {
  isEmoji.value = false;
  if (chatRecordList.value.length > 0) {
    Object.values(backBtnRefs).forEach(el => {
      if (el) el.style.display = "none";
    });
  }
}

function translationStart() {
  voiceActive.value = true;
  rc = new Recorderx();
  nextTick(() => {
    rc
      .start()
      .then(() => {
        startVoiceTime.value = new Date();
        console.log("start recording");
      })
      .catch(error => {
        console.log("Recording failed.", error);
      });
  });
}

function translationEnd() {
  console.log("结束");
  voiceActive.value = false;
  rc.pause();
  if (new Date() - startVoiceTime.value < 1000) {
    toast({ type: "error", message: "按键时间太短" });
    return false;
  }
  var wav = rc.getRecord({
    encodeTo: ENCODE_TYPE.WAV
  });
  var file = new File([wav], "voice.wav", {
    type: wav.type
  });
  var formData = new window.FormData();
  formData.append("file", file);
  formData.append("type", 5);
  formData.append("nickname", nickname.value);
  formData.append("avatar", avatar.value);
  if (store.userId != null) {
    formData.append("userId", store.userId);
  }
  formData.append("ipAddress", ipAddress.value);
  formData.append("ipSource", ipSource.value);
  var options = {
    url: "/api/voice",
    data: formData,
    method: "post",
    headers: {
      "Content-Type": "multipart/form-data"
    }
  };
  axios(options);
}

function translationmove() {}

function playVoice(item) {
  var player = voicesRefs[item.id];
  if (!player) return;
  var playEl = playsRefs[item.id];
  var pauseEl = pausesRefs[item.id];
  if (player.paused) {
    player.play();
    if (playEl) playEl.$el.style.display = "none";
    if (pauseEl) pauseEl.$el.style.display = "inline-flex";
  } else {
    if (playEl) playEl.$el.style.display = "inline-flex";
    if (pauseEl) pauseEl.$el.style.display = "none";
    player.pause();
  }
}

function endVoice(item) {
  var playEl = playsRefs[item.id];
  var pauseEl = pausesRefs[item.id];
  if (playEl) playEl.$el.style.display = "inline-flex";
  if (pauseEl) pauseEl.$el.style.display = "none";
}

function getVoiceTime(item) {
  var player = voicesRefs[item.id];
  if (!player) return;
  var time = player.duration;
  time = Math.ceil(time);
  var str = "...";
  for (var i = 0; i < time; i++) {
    if (i % 2 == 0) {
      str += ".";
    }
  }
  var timeEl = voiceTimesRefs[item.id];
  if (timeEl) {
    timeEl.innerHTML = " " + str + " " + time + " ''";
  }
}

onUpdated(() => {
  var ele = document.getElementById("message");
  if (ele) {
    ele.scrollTop = ele.scrollHeight;
  }
});

onBeforeUnmount(() => {
  if (heartBeat) {
    clearInterval(heartBeat);
  }
  if (websocket.value) {
    websocket.value.close();
  }
});
</script>

<style scoped>
@media (min-width: 760px) {
  .chat-container {
    position: fixed;
    color: #4c4948 !important;
    bottom: 104px;
    right: 20px;
    height: calc(85% - 64px - 20px);
    max-height: 590px !important;
    min-height: 250px !important;
    width: 400px !important;
    border-radius: 16px !important;
  }
  .close {
    display: none;
  }
}
@media (max-width: 760px) {
  .chat-container {
    position: fixed;
    top: 0;
    bottom: 0;
    right: 0;
    left: 0;
  }
  .close {
    display: block;
    margin-left: auto;
  }
}
.chat-container {
  box-shadow: 0 5px 40px rgba(0, 0, 0, 0.16) !important;
  font-size: 14px;
  background: #f4f6fb;
  z-index: 1200;
}
.chat-btn {
  background: #1f93ff;
  border-radius: 100px !important;
  position: fixed;
  bottom: 15px;
  right: 5px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.16) !important;
  cursor: pointer;
  height: 60px !important;
  width: 60px !important;
  z-index: 1000 !important;
  user-select: none;
}
.header {
  display: flex;
  align-items: center;
  padding: 20px 24px;
  background: #ffffff;
  border-radius: 1rem 1rem 0 0;
  box-shadow: 0 10px 15px -16px rgba(50, 50, 93, 0.08),
    0 4px 6px -8px rgba(50, 50, 93, 0.04);
}
.footer {
  padding: 8px 16px;
  position: absolute;
  width: 100%;
  bottom: 0;
  background: #f7f7f7;
  border-radius: 0 0 1rem 1rem;
  display: flex;
  align-items: center;
}
.footer textarea {
  background: #fff;
  padding-left: 10px;
  padding-top: 8px;
  width: 100%;
  height: 32px;
  outline: none;
  resize: none;
  overflow: hidden;
  font-size: 13px;
}
.voice-btn {
  font-size: 13px;
  outline: none;
  height: 32px;
  width: 100%;
  background: #fff;
  border-radius: 2px;
}
.message {
  position: absolute;
  width: 100%;
  padding: 20px 16px 0 16px;
  top: 80px;
  bottom: 50px;
  overflow-y: auto;
  overflow-x: hidden;
}
.text {
  color: #999;
  text-align: center;
  font-size: 12px;
  margin-bottom: 12px;
}
.user-message {
  display: flex;
  margin-bottom: 10px;
}
.my-message {
  display: flex;
  margin-bottom: 10px;
  justify-content: flex-end;
}
.left-avatar {
  margin-right: 10px;
}
.right-avatar {
  order: 1;
  margin-left: 10px;
}
.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}
.nickname {
  display: flex;
  align-items: center;
  font-size: 12px;
  margin-top: 3px;
  margin-bottom: 5px;
}
.user-content {
  position: relative;
  background-color: #fff;
  padding: 10px;
  border-radius: 5px 20px 20px 20px;
  width: fit-content;
  white-space: pre-line;
  word-wrap: break-word;
  word-break: break-all;
}
.my-content {
  position: relative;
  border-radius: 20px 5px 20px 20px;
  padding: 12px;
  background: #12b7f5;
  color: #fff;
  white-space: pre-line;
  word-wrap: break-word;
  word-break: break-all;
}
.submit-btn {
  color: rgb(31, 147, 255);
}
.emoji {
  cursor: pointer;
  font-size: 1.3rem;
  margin: 0 8px;
}
.emoji-box {
  position: absolute;
  box-shadow: 0 8px 16px rgba(50, 50, 93, 0.08), 0 4px 12px rgba(0, 0, 0, 0.07);
  background: #fff;
  border-radius: 8px;
  right: 20px;
  bottom: 52px;
  height: 180px;
  width: 300px;
  overflow-y: auto;
  padding: 6px 16px;
}
.emoji-border:before {
  display: block;
  height: 0;
  width: 0;
  content: "";
  border-left: 14px solid transparent;
  border-right: 14px solid transparent;
  border-top: 12px solid #fff;
  bottom: 40px;
  position: absolute;
  right: 43px;
}
.unread {
  text-align: center;
  border-radius: 50%;
  font-size: 14px;
  height: 20px;
  width: 20px;
  position: absolute;
  background: #f24f2d;
  color: #fff;
}
.back-menu {
  font-size: 13px;
  border-radius: 2px;
  position: absolute;
  background: rgba(255, 255, 255, 0.9);
  color: #000;
  width: 80px;
  height: 35px;
  text-align: center;
  line-height: 35px;
  display: none;
}
.voice {
  position: fixed;
  z-index: 1500;
  bottom: 52px;
  left: 0;
  right: 0;
  top: 80px;
  background: rgba(0, 0, 0, 0.8);
}
.close-voice {
  position: absolute;
  bottom: 60px;
  left: 30px;
  display: inline-block;
  height: 50px;
  width: 50px;
  line-height: 50px;
  border-radius: 50%;
  text-align: center;
  background: #fff;
}
</style>
