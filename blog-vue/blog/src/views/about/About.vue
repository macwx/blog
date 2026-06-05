<template>
  <div>
    <!-- banner -->
    <div class="banner" :style="cover">
      <h1 class="banner-title">关于我</h1>
    </div>
    <!-- 关于我内容 -->
    <v-card class="blog-container">
      <!-- 博主头像 -->
      <div class="my-wrapper">
        <v-avatar size="110">
          <img class="author-avatar" :src="avatar" />
        </v-avatar>
      </div>
      <!-- 介绍 -->
      <div
        ref="aboutRef"
        class="about-content markdown-body"
        v-html="aboutContent"
      />
    </v-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from "vue";
import Clipboard from "clipboard";
import markdownToHtml from "../../utils/markdown";
import axios from "axios";
import { useBlogStore } from "@/store";
import { useToast } from "@/components/toast/useToast";

const store = useBlogStore();
const toast = useToast();

const aboutRef = ref(null);
const aboutContent = ref("");
let clipboard = null;
const imgList = ref([]);

function getAboutContent() {
  axios.get("/api/about").then(({ data }) => {
    aboutContent.value = markdownToHtml(data.data);
    nextTick(() => {
      // 添加代码复制功能
      clipboard = new Clipboard(".copy-btn");
      clipboard.on("success", () => {
        toast({ type: "success", message: "复制成功" });
      });
      // 添加图片预览功能
      if (aboutRef.value) {
        const imgs = aboutRef.value.getElementsByTagName("img");
        for (var i = 0; i < imgs.length; i++) {
          imgList.value.push(imgs[i].src);
          imgs[i].addEventListener("click", function(e) {
            previewImg(e.target.currentSrc);
          });
        }
      }
    });
  });
}

function previewImg(img) {
  // TODO: Vue3 migration - $imagePreview removed, use basic preview
  window.open(img, "_blank");
}

const avatar = computed(() => {
  return store.blogInfo.websiteConfig.websiteAvatar;
});

const cover = computed(() => {
  var c = "";
  store.blogInfo.pageList.forEach(item => {
    if (item.pageLabel == "about") {
      c = item.pageCover;
    }
  });
  return "background: url(" + c + ") center center / cover no-repeat";
});

onMounted(() => {
  getAboutContent();
});

onUnmounted(() => {
  if (clipboard) {
    clipboard.destroy();
  }
});
</script>

<style scoped>
.about-content {
  word-break: break-word;
  line-height: 1.8;
}
.my-wrapper {
  text-align: center;
}
.author-avatar {
  transition: all 0.5s;
}
.author-avatar:hover {
  transform: rotate(360deg);
}
</style>

<style lang="scss">
pre.hljs {
  padding: 12px 2px 12px 40px !important;
  border-radius: 5px !important;
  position: relative;
  font-size: 14px !important;
  line-height: 22px !important;
  overflow: hidden !important;
  &:hover .copy-btn {
    display: flex;
    justify-content: center;
    align-items: center;
  }
  code {
    display: block !important;
    margin: 0 10px !important;
    overflow-x: auto !important;
    &::-webkit-scrollbar {
      z-index: 11;
      width: 6px;
    }
    &::-webkit-scrollbar:horizontal {
      height: 6px;
    }
    &::-webkit-scrollbar-thumb {
      border-radius: 5px;
      width: 6px;
      background: #666;
    }
    &::-webkit-scrollbar-corner,
    &::-webkit-scrollbar-track {
      background: #1e1e1e;
    }
    &::-webkit-scrollbar-track-piece {
      background: #1e1e1e;
      width: 6px;
    }
  }
  .line-numbers-rows {
    position: absolute;
    pointer-events: none;
    top: 12px;
    bottom: 12px;
    left: 0;
    font-size: 100%;
    width: 40px;
    text-align: center;
    letter-spacing: -1px;
    border-right: 1px solid rgba(0, 0, 0, 0.66);
    user-select: none;
    counter-reset: linenumber;
    span {
      pointer-events: none;
      display: block;
      counter-increment: linenumber;
      &:before {
        content: counter(linenumber);
        color: #999;
        display: block;
        text-align: center;
      }
    }
  }
  b.name {
    position: absolute;
    top: 7px;
    right: 45px;
    z-index: 1;
    color: #999;
    pointer-events: none;
  }
  .copy-btn {
    position: absolute;
    top: 6px;
    right: 6px;
    z-index: 1;
    color: #ccc;
    background-color: #525252;
    border-radius: 6px;
    display: none;
    font-size: 14px;
    width: 32px;
    height: 24px;
    outline: none;
  }
}
</style>
