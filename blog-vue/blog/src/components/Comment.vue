<template>
  <div>
    <div class="comment-title"><i class="iconfont iconpinglunzu" />评论</div>
    <!-- 评论框 -->
    <div class="comment-input-wrapper">
      <div style="display:flex">
        <v-avatar size="40">
          <img
            v-if="store.avatar"
            :src="store.avatar"
          />
          <img
            v-else
            :src="store.blogInfo.websiteConfig.touristAvatar"
          />
        </v-avatar>
        <div style="width:100%" class="ml-3">
          <div class="comment-input">
            <textarea
              class="comment-textarea"
              v-model="commentContent"
              placeholder="留下点什么吧..."
              auto-grow
              dense
            />
          </div>
          <!-- 操作按钮 -->
          <div class="emoji-container">
            <span
              :class="chooseEmoji ? 'emoji-btn-active' : 'emoji-btn'"
              @click="chooseEmoji = !chooseEmoji"
            >
              <i class="iconfont iconbiaoqing" />
            </span>
            <button
              @click="insertComment"
              class="upload-btn v-comment-btn"
              style="margin-left:auto"
            >
              提交
            </button>
          </div>
          <!-- 表情框 -->
          <emoji @addEmoji="addEmoji" :chooseEmoji="chooseEmoji" />
        </div>
      </div>
    </div>
    <!-- 评论详情 -->
    <div v-if="count > 0 && reFresh">
      <!-- 评论数量 -->
      <div class="count">{{ count }} 评论</div>
      <!-- 评论列表 -->
      <div
        style="display:flex"
        class="pt-5"
        v-for="(item, index) of commentList"
        :key="item.id"
      >
        <!-- 头像 -->
        <v-avatar size="40" class="comment-avatar">
          <img :src="item.avatar" />
        </v-avatar>
        <div class="comment-meta">
          <!-- 用户名 -->
          <div class="comment-user">
            <span v-if="!item.webSite">{{ item.nickname }}</span>
            <a v-else :href="item.webSite" target="_blank">
              {{ item.nickname }}
            </a>
            <span class="blogger-tag" v-if="item.userId == 1">博主</span>
          </div>
          <!-- 信息 -->
          <div class="comment-info">
            <!-- 楼层 -->
            <span style="margin-right:10px">{{ count - index }}楼</span>
            <!-- 发表时间 -->
            <span style="margin-right:10px">{{ formatDate(item.createTime) }}</span>
            <!-- 点赞 -->
            <span
              :class="isLike(item.id) + ' iconfont icondianzan'"
              @click="like(item)"
            />
            <span v-show="item.likeCount > 0"> {{ item.likeCount }}</span>
            <!-- 回复 -->
            <span class="reply-btn" @click="replyComment(index, item)">
              回复
            </span>
          </div>
          <!-- 评论内容 -->
          <p v-html="item.commentContent" class="comment-content"></p>
          <!-- 回复人 -->
          <div
            style="display:flex"
            v-for="reply of item.replyDTOList"
            :key="reply.id"
          >
            <!-- 头像 -->
            <v-avatar size="36" class="comment-avatar">
              <img :src="reply.avatar" />
            </v-avatar>
            <div class="reply-meta">
              <!-- 用户名 -->
              <div class="comment-user">
                <span v-if="!reply.webSite">{{ reply.nickname }}</span>
                <a v-else :href="reply.webSite" target="_blank">
                  {{ reply.nickname }}
                </a>
                <span class="blogger-tag" v-if="reply.userId == 1">博主</span>
              </div>
              <!-- 信息 -->
              <div class="comment-info">
                <!-- 发表时间 -->
                <span style="margin-right:10px">
                  {{ formatDate(reply.createTime) }}
                </span>
                <!-- 点赞 -->
                <span
                  :class="isLike(reply.id) + ' iconfont icondianzan'"
                  @click="like(reply)"
                />
                <span v-show="reply.likeCount > 0"> {{ reply.likeCount }}</span>
                <!-- 回复 -->
                <span class="reply-btn" @click="replyComment(index, reply)">
                  回复
                </span>
              </div>
              <!-- 回复内容 -->
              <p class="comment-content">
                <!-- 回复用户名 -->
                <template v-if="reply.replyUserId != item.userId">
                  <span v-if="!reply.replyWebSite" class="ml-1">
                    @{{ reply.replyNickname }}
                  </span>
                  <a
                    v-else
                    :href="reply.replyWebSite"
                    target="_blank"
                    class="comment-nickname ml-1"
                  >
                    @{{ reply.replyNickname }}
                  </a>
                  ,
                </template>
                <span v-html="reply.commentContent" />
              </p>
            </div>
          </div>
          <!-- 回复数量 -->
          <div
            class="mb-3"
            style="font-size:0.75rem;color:#6d757a"
            v-show="item.replyCount > 3"
            :ref="el => { if (el) checkRefs[index] = el }"
          >
            共
            <b>{{ item.replyCount }}</b>
            条回复，
            <span
              style="color:#00a1d6;cursor:pointer"
              @click="checkReplies(index, item)"
            >
              点击查看
            </span>
          </div>
          <!-- 回复分页 -->
          <div
            class="mb-3"
            style="font-size:0.75rem;color:#222;display:none"
            :ref="el => { if (el) pagingRefs[index] = el }"
          >
            <span style="padding-right:10px">
              共{{ Math.ceil(item.replyCount / 5) }}页
            </span>
            <paging
              :ref="el => { if (el) pageRefs[index] = el }"
              :totalPage="Math.ceil(item.replyCount / 5)"
              :index="index"
              :commentId="item.id"
              @changeReplyCurrent="changeReplyCurrent"
            />
          </div>
          <!-- 回复框 -->
          <Reply :type="type" :ref="el => { if (el) replyRefs[index] = el }" @reloadReply="reloadReply" />
        </div>
      </div>
      <!-- 加载按钮 -->
      <div class="load-wrapper">
        <v-btn variant="outlined" v-if="count > commentList.length" @click="listComments">
          加载更多...
        </v-btn>
      </div>
    </div>
    <!-- 没有评论提示 -->
    <div v-else style="padding:1.25rem;text-align:center">
      来发评论吧~
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch, nextTick, onMounted } from "vue";
import { useRoute } from "vue-router";
import axios from "axios";
import { useBlogStore } from "@/store";
import { useToast } from "@/components/toast/useToast";
import Reply from "./Reply.vue";
import Paging from "./Paging.vue";
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

const emit = defineEmits(["getCommentCount"]);

// Template refs
const replyRefs = reactive({});
const checkRefs = reactive({});
const pagingRefs = reactive({});
const pageRefs = reactive({});

const reFresh = ref(true);
const commentContent = ref("");
const chooseEmoji = ref(false);
const current = ref(1);
const commentList = ref([]);
const count = ref(0);

function formatDate(time) {
  // TODO: Vue3 migration - replaced | date filter
  return time;
}

function isLike(commentId) {
  return store.commentLikeSet.indexOf(commentId) != -1 ? "like-active" : "like";
}

function replyComment(index, item) {
  // Hide all reply boxes
  Object.values(replyRefs).forEach(ref => {
    if (ref && ref.replyRef) {
      ref.replyRef.style.display = "none";
    }
  });
  // Set values on the target reply box
  var replyComp = replyRefs[index];
  if (replyComp) {
    replyComp.commentContent = "";
    replyComp.nickname = item.nickname;
    replyComp.replyUserId = item.userId;
    replyComp.parentId = commentList.value[index].id;
    replyComp.chooseEmoji = false;
    replyComp.index = index;
    if (replyComp.replyRef) {
      replyComp.replyRef.style.display = "block";
    }
  }
}

function addEmoji(key) {
  commentContent.value += key;
}

function checkReplies(index, item) {
  axios
    .get("/api/comments/" + item.id + "/replies", {
      params: { current: 1, size: 5 }
    })
    .then(({ data }) => {
      if (checkRefs[index]) {
        checkRefs[index].style.display = "none";
      }
      item.replyDTOList = data.data;
      if (Math.ceil(item.replyCount / 5) > 1) {
        if (pagingRefs[index]) {
          pagingRefs[index].style.display = "flex";
        }
      }
    });
}

function changeReplyCurrent(currentVal, index, commentId) {
  axios
    .get("/api/comments/" + commentId + "/replies", {
      params: { current: currentVal, size: 5 }
    })
    .then(({ data }) => {
      commentList.value[index].replyDTOList = data.data;
    });
}

function listComments() {
  const path = route.path;
  const arr = path.split("/");
  var param = {
    current: current.value,
    type: props.type
  };
  switch (props.type) {
    case 1:
    case 3:
      param.topicId = arr[2];
      break;
    default:
      break;
  }
  axios
    .get("/api/comments", {
      params: param
    })
    .then(({ data }) => {
      if (current.value == 1) {
        commentList.value = data.data.recordList;
      } else {
        commentList.value.push(...data.data.recordList);
      }
      current.value++;
      count.value = data.data.count;
      emit("getCommentCount", count.value);
    });
}

function insertComment() {
  if (!store.userId) {
    store.loginFlag = true;
    return false;
  }
  if (commentContent.value.trim() == "") {
    toast({ type: "error", message: "评论不能为空" });
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
    commentContent: commentContent.value,
    type: props.type
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
      current.value = 1;
      listComments();
      const isReview = store.blogInfo.websiteConfig.isCommentReview;
      if (isReview) {
        toast({ type: "warnning", message: "评论成功，正在审核中" });
      } else {
        toast({ type: "success", message: "评论成功" });
      }
    } else {
      toast({ type: "error", message: data.message });
    }
  });
}

function like(comment) {
  if (!store.userId) {
    store.loginFlag = true;
    return false;
  }
  axios
    .post("/api/comments/" + comment.id + "/like")
    .then(({ data }) => {
      if (data.flag) {
        if (store.commentLikeSet.indexOf(comment.id) != -1) {
          comment.likeCount = comment.likeCount - 1;
        } else {
          comment.likeCount = comment.likeCount + 1;
        }
        store.commentLike(comment.id);
      }
    });
}

function reloadReply(index) {
  var pageComp = pageRefs[index];
  var currentPage = pageComp ? pageComp.current : 1;
  axios
    .get("/api/comments/" + commentList.value[index].id + "/replies", {
      params: {
        current: currentPage,
        size: 5
      }
    })
    .then(({ data }) => {
      commentList.value[index].replyCount++;
      if (commentList.value[index].replyCount > 5) {
        if (pagingRefs[index]) {
          pagingRefs[index].style.display = "flex";
        }
      }
      if (checkRefs[index]) {
        checkRefs[index].style.display = "none";
      }
      var replyComp = replyRefs[index];
      if (replyComp && replyComp.replyRef) {
        replyComp.replyRef.style.display = "none";
      }
      commentList.value[index].replyDTOList = data.data;
    });
}

watch(commentList, () => {
  reFresh.value = false;
  nextTick(() => {
    reFresh.value = true;
  });
});

onMounted(() => {
  listComments();
});
</script>

<style scoped>
.blogger-tag {
  background: #ffa51e;
  font-size: 12px;
  display: inline-block;
  border-radius: 2px;
  color: #fff;
  padding: 0 5px;
  margin-left: 6px;
}
.comment-title {
  display: flex;
  align-items: center;
  font-size: 1.25rem;
  font-weight: bold;
  line-height: 40px;
  margin-bottom: 10px;
}
.comment-title i {
  font-size: 1.5rem;
  margin-right: 5px;
}
.comment-input-wrapper {
  border: 1px solid #90939950;
  border-radius: 4px;
  padding: 10px;
  margin: 0 0 10px;
}
.count {
  padding: 5px;
  line-height: 1.75;
  font-size: 1.25rem;
  font-weight: bold;
}
.comment-meta {
  margin-left: 0.8rem;
  width: 100%;
  border-bottom: 1px dashed #f5f5f5;
}
.reply-meta {
  margin-left: 0.8rem;
  width: 100%;
}
.comment-user {
  font-size: 14px;
  line-height: 1.75;
}
.comment-user a {
  color: #1abc9c !important;
  font-weight: 500;
  transition: 0.3s all;
}
.comment-nickname {
  text-decoration: none;
  color: #1abc9c !important;
  font-weight: 500;
}
.comment-info {
  line-height: 1.75;
  font-size: 0.75rem;
  color: #b3b3b3;
}
.reply-btn {
  cursor: pointer;
  float: right;
  color: #ef2f11;
}
.comment-content {
  font-size: 0.875rem;
  line-height: 1.75;
  padding-top: 0.625rem;
  white-space: pre-line;
  word-wrap: break-word;
  word-break: break-all;
}
.comment-avatar {
  transition: all 0.5s;
}
.comment-avatar:hover {
  transform: rotate(360deg);
}
.like {
  cursor: pointer;
  font-size: 0.875rem;
}
.like:hover {
  color: #eb5055;
}
.like-active {
  cursor: pointer;
  font-size: 0.875rem;
  color: #eb5055;
}
.load-wrapper {
  margin-top: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
}
.load-wrapper button {
  background-color: #49b1f5;
  color: #fff;
}
</style>
