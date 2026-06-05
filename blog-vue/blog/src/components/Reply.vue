<template>
  <div class="reply-input-wrapper" style="display: none" ref="replyRef">
    <textarea
      class="comment-textarea"
      :placeholder="'回复 @' + nickname + '：'"
      auto-grow
      dense
      v-model="commentContent"
    />
    <div class="emoji-container">
      <span
        :class="chooseEmoji ? 'emoji-btn-active' : 'emoji-btn'"
        @click="chooseEmoji = !chooseEmoji"
      >
        <i class="iconfont iconbiaoqing" />
      </span>
      <div style="margin-left:auto">
        <button @click="cancleReply" class="cancle-btn v-comment-btn">
          取消
        </button>
        <button @click="insertReply" class="upload-btn v-comment-btn">
          提交
        </button>
      </div>
    </div>
    <!-- 表情框 -->
    <emoji @addEmoji="addEmoji" :chooseEmoji="chooseEmoji" />
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRoute } from "vue-router";
import axios from "axios";
import { useBlogStore } from "@/store";
import { useToast } from "@/components/toast/useToast";
import Emoji from "./Emoji.vue";
import EmojiList from "../assets/js/emoji";

const store = useBlogStore();
const route = useRoute();
const toast = useToast();

const props = defineProps({
  type: {
    type: Number
  }
});

const emit = defineEmits(["reloadReply"]);

const replyRef = ref(null);
const index = ref(0);
const chooseEmoji = ref(false);
const nickname = ref("");
const replyUserId = ref(null);
const parentId = ref(null);
const commentContent = ref("");

function cancleReply() {
  if (replyRef.value) {
    replyRef.value.style.display = "none";
  }
}

function insertReply() {
  if (!store.userId) {
    store.loginFlag = true;
    return false;
  }
  if (commentContent.value.trim() == "") {
    toast({ type: "error", message: "回复不能为空" });
    return false;
  }
  var reg = /\[.+?\]/g;
  commentContent.value = commentContent.value.replace(reg, function(str) {
    return (
      "<img src= '" +
      EmojiList[str] +
      "' width='24'height='24' style='margin: 0 1px;vertical-align: text-bottom'/>"
    );
  });
  const path = route.path;
  const arr = path.split("/");
  var comment = {
    type: props.type,
    replyUserId: replyUserId.value,
    parentId: parentId.value,
    commentContent: commentContent.value
  };
  switch (props.type) {
    case 1:
    case 3:
      comment.topicId = arr[2];
      break;
    default:
      break;
  }
  commentContent.value = "";
  axios.post("/api/comments", comment).then(({ data }) => {
    if (data.flag) {
      emit("reloadReply", index.value);
      toast({ type: "success", message: "回复成功" });
    } else {
      toast({ type: "error", message: data.message });
    }
  });
}

function addEmoji(text) {
  commentContent.value += text;
}

// Expose properties for parent component to set
defineExpose({
  replyRef,
  commentContent,
  nickname,
  replyUserId,
  parentId,
  chooseEmoji,
  index
});
</script>

<style scoped>
.reply-input-wrapper {
  border: 1px solid #90939950;
  border-radius: 4px;
  padding: 10px;
  margin: 0 0 10px;
}
</style>
