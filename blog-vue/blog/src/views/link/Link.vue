<template>
  <div>
    <!-- banner -->
    <div class="banner" :style="cover">
      <h1 class="banner-title">友情链接</h1>
    </div>
    <!-- 链接列表 -->
    <v-card class="blog-container">
      <div class="link-title mb-1">
        <v-icon color="blue">mdi-link-variant</v-icon> 大佬链接
      </div>
      <v-row class="link-container">
        <v-col
          class="link-wrapper"
          md="4"
          cols="12"
          v-for="item of friendLinkList"
          :key="item.id"
        >
          <a :href="item.linkAddress" target="_blank">
            <v-avatar size="65" class="link-avatar">
              <img :src="item.linkAvatar" />
            </v-avatar>
            <div style="width:100%;z-index:10;">
              <div class="link-name">{{ item.linkName }}</div>
              <div class="link-intro">{{ item.linkIntro }}</div>
            </div>
          </a>
        </v-col>
      </v-row>
      <!-- 说明 -->
      <div class="link-title mt-4 mb-4">
        <v-icon color="blue">mdi-dots-horizontal-circle</v-icon> 添加友链
      </div>
      <blockquote>
        <div>名称：{{ blogInfo.websiteConfig.websiteName }}</div>
        <div>简介：{{ blogInfo.websiteConfig.websiteIntro }}</div>
        <div>头像：{{ blogInfo.websiteConfig.websiteAvatar }}</div>
        <div>地址：{{ blogInfo.websiteConfig.websiteAddress }}</div>
      </blockquote>
      <div class="mt-5 mb-5">
        需要交换友链的可在下方留言
      </div>
      <blockquote class="mb-10">
        友链信息展示需要，你的信息格式要包含：名称、介绍、链接、头像
      </blockquote>
      <!-- 评论 -->
      <Comment :type="commentType" />
    </v-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import axios from "axios";
import { useBlogStore } from "@/store";
import Comment from "../../components/Comment.vue";

const store = useBlogStore();

const friendLinkList = ref([]);
const commentType = ref(2);

function listFriendLink() {
  axios.get("/api/links").then(({ data }) => {
    friendLinkList.value = data.data;
  });
}

const blogInfo = computed(() => {
  return store.blogInfo;
});

const cover = computed(() => {
  var c = "";
  store.blogInfo.pageList.forEach(item => {
    if (item.pageLabel == "link") {
      c = item.pageCover;
    }
  });
  return "background: url(" + c + ") center center / cover no-repeat";
});

onMounted(() => {
  listFriendLink();
});
</script>

<style scoped>
blockquote {
  line-height: 2;
  margin: 0;
  font-size: 15px;
  border-left: 0.2rem solid #49b1f5;
  padding: 10px 1rem !important;
  background-color: #ecf7fe;
  border-radius: 4px;
}
.link-title {
  color: #344c67;
  font-size: 21px;
  font-weight: bold;
  line-height: 2;
}
.link-container {
  margin: 10px 10px 0;
}
.link-wrapper {
  position: relative;
  transition: all 0.3s;
  border-radius: 8px;
}
.link-avatar {
  margin-top: 5px;
  margin-left: 10px;
  transition: all 0.5s;
}
@media (max-width: 759px) {
  .link-avatar {
    margin-left: 30px;
  }
}
.link-name {
  text-align: center;
  font-size: 1.25rem;
  font-weight: bold;
  z-index: 1000;
}
.link-intro {
  text-align: center;
  padding: 16px 10px;
  height: 50px;
  font-size: 13px;
  color: #1f2d3d;
  width: 100%;
}
.link-wrapper:hover a {
  color: #fff;
}
.link-wrapper:hover .link-intro {
  color: #fff;
}
.link-wrapper:hover .link-avatar {
  transform: rotate(360deg);
}
.link-wrapper a {
  color: #333;
  text-decoration: none;
  display: flex;
  height: 100%;
  width: 100%;
}
.link-wrapper:hover {
  box-shadow: 0 2px 20px #49b1f5;
}
.link-wrapper:hover:before {
  transform: scale(1);
}
.link-wrapper:before {
  position: absolute;
  border-radius: 8px;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  background: #49b1f5 !important;
  content: "";
  transition-timing-function: ease-out;
  transition-duration: 0.3s;
  transition-property: transform;
  transform: scale(0);
}
</style>
