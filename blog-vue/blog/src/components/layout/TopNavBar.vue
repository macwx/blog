<template>
  <v-app-bar app :class="navClass" hide-on-scroll flat height="60">
    <!-- 手机端导航栏 -->
    <div class="d-md-none nav-mobile-container">
      <div style="font-size:18px;font-weight:bold">
        <router-link to="/">
          {{ blogInfo.websiteConfig.websiteAuthor }}
        </router-link>
      </div>
      <div style="margin-left:auto">
        <a @click="openSearch"><i class="iconfont iconsousuo"/></a>
        <a @click="openDrawer" style="margin-left:10px;font-size:20px">
          <i class="iconfont iconhanbao" />
        </a>
      </div>
    </div>
    <!-- 电脑导航栏 -->
    <div class="d-md-block d-none nav-container">
      <div class="float-left blog-title">
        <router-link to="/">
          {{ blogInfo.websiteConfig.websiteAuthor }}
        </router-link>
      </div>
      <div class="float-right nav-title">

        <div class="menus-item">
          <router-link class="menu-btn" to="/">
            <i class="iconfont iconzhuye" /> 首页
          </router-link>
        </div>
        <div class="menus-item">
          <v-menu open-on-hover offset-y>
            <template v-slot:activator="{ props }">
              <a class="menu-btn" v-bind="props">
                <i class="iconfont iconfaxian" /> 发现
                <i class="iconfont iconxiangxia2 expand" />
              </a>
            </template>
            <v-list class="nav-dropdown-list" density="compact">
              <v-list-item to="/archives" class="nav-dropdown-item">
                <v-list-item-title><i class="iconfont iconguidang" /> 归档</v-list-item-title>
              </v-list-item>
              <v-list-item to="/categories" class="nav-dropdown-item">
                <v-list-item-title><i class="iconfont iconfenlei" /> 分类</v-list-item-title>
              </v-list-item>
              <v-list-item to="/tags" class="nav-dropdown-item">
                <v-list-item-title><i class="iconfont iconbiaoqian" /> 标签</v-list-item-title>
              </v-list-item>
            </v-list>
          </v-menu>
        </div>
        <div class="menus-item">
          <v-menu open-on-hover offset-y>
            <template v-slot:activator="{ props }">
              <a class="menu-btn" v-bind="props">
                <i class="iconfont iconqita" /> 杂谈
                <i class="iconfont iconxiangxia2 expand" />
              </a>
            </template>
            <v-list class="nav-dropdown-list" density="compact">
              <v-list-item to="/albums" class="nav-dropdown-item">
                <v-list-item-title><i class="iconfont iconxiangce1" /> 相册</v-list-item-title>
              </v-list-item>
              <v-list-item to="/talks" class="nav-dropdown-item">
                <v-list-item-title><i class="iconfont iconpinglun" /> 说说</v-list-item-title>
              </v-list-item>
            </v-list>
          </v-menu>
        </div>
        <div class="menus-item">
          <v-menu open-on-hover offset-y>
            <template v-slot:activator="{ props }">
              <a class="menu-btn" v-bind="props">
                <i class="iconfont iconhanbao" /> 网盘
                <i class="iconfont iconxiangxia2 expand" />
              </a>
            </template>
            <v-list class="nav-dropdown-list" density="compact">
              <v-list-item href="https://cloud.henaumcw.top/" target="_blank" class="nav-dropdown-item">
                <v-list-item-title><i class="iconfont iconhanbao" /> Cloud</v-list-item-title>
              </v-list-item>
              <v-list-item href="https://yun.macw.cc/" target="_blank" class="nav-dropdown-item">
                <v-list-item-title><i class="iconfont iconhanbao" /> File</v-list-item-title>
              </v-list-item>
            </v-list>
          </v-menu>
        </div>
        <div class="menus-item">
          <router-link class="menu-btn" to="/links">
            <i class="iconfont iconlianjie" /> 友链
          </router-link>
        </div>
        <div class="menus-item">
          <router-link class="menu-btn" to="/about">
            <i class="iconfont iconzhifeiji" /> 关于
          </router-link>
        </div>
        <div class="menus-item">
          <router-link class="menu-btn" to="/message">
            <i class="iconfont iconpinglunzu" /> 留言
          </router-link>
        </div>
        <div class="menus-item">
          <router-link class="menu-btn" to="/clawdchat">
            <i class="iconfont iconfaxian" /> 社区
          </router-link>
        </div>
        <div class="menus-item">
          <a
            class="menu-btn"
            v-if="!store.avatar"
            @click="openLogin"
          >
            <i class="iconfont icondenglu" /> 登录
          </a>
          <template v-else>
            <v-menu open-on-hover offset-y>
              <template v-slot:activator="{ props }">
                <img
                  class="user-avatar"
                  :src="store.avatar"
                  height="30"
                  width="30"
                  v-bind="props"
                />
              </template>
              <v-list class="nav-dropdown-list" density="compact">
                <v-list-item to="/user" class="nav-dropdown-item">
                  <v-list-item-title><i class="iconfont icongerenzhongxin" /> 个人中心</v-list-item-title>
                </v-list-item>
                <v-list-item @click="logout" class="nav-dropdown-item">
                  <v-list-item-title><i class="iconfont icontuichu" /> 退出</v-list-item-title>
                </v-list-item>
              </v-list>
            </v-menu>
          </template>
        </div>
        <div class="menus-item">
          <a class="menu-btn" @click="openSearch">
            <i class="iconfont iconsousuo" /> 搜索
          </a>
        </div>
      </div>
    </div>
  </v-app-bar>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useBlogStore } from "@/store";
import { useToast } from "@/components/toast/useToast";
import axios from "axios";

const store = useBlogStore();
const router = useRouter();
const route = useRoute();
const toast = useToast();

const navClass = ref("");

const blogInfo = computed(() => store.blogInfo);

function scroll() {
  let scrollTop =
    window.pageYOffset ||
    document.documentElement.scrollTop ||
    document.body.scrollTop;
  if (scrollTop > 60) {
    navClass.value = "nav-fixed";
  } else {
    navClass.value = "nav";
  }
}

function openSearch() {
  store.searchFlag = true;
}

function openDrawer() {
  store.drawer = true;
}

function openLogin() {
  store.loginFlag = true;
}

function logout() {
  if (route.path === "/user") {
    router.go(-1);
  }
  axios.get("/api/logout").then(({ data }) => {
    if (data.flag) {
      store.logout();
      toast({ type: "success", message: "注销成功" });
    } else {
      toast({ type: "error", message: data.message });
    }
  });
}

onMounted(() => {
  // 初始化导航栏状态：刷新时浏览器可能恢复滚动位置但不再触发 scroll 事件，
  // 且留言板等满屏页面窗口无法滚动，必须主动算一次初始值
  scroll();
  window.addEventListener("scroll", scroll, { passive: true });
});

onUnmounted(() => {
  window.removeEventListener("scroll", scroll);
});
</script>

<style scoped>
i {
  margin-right: 4px;
}
.nav {
  background: rgba(0, 0, 0, 0) !important;
}
.nav-container {
  font-size: 14px;
  width: 100%;
  height: 100%;
}
.nav-mobile-container {
  width: 100%;
  display: flex;
  align-items: center;
}
.blog-title,
.nav-title {
  display: flex;
  align-items: center;
  height: 100%;
}
.blog-title a {
  font-size: 18px;
  font-weight: bold;
}
</style>

<style>
/* TopNavBar global styles - unscoped for dropdown to work */
.v-toolbar,
.v-toolbar__content,
.v-toolbar__extension {
  overflow: visible !important;
}
.v-toolbar .blog-title a {
  color: #eee !important;
}
.v-toolbar.nav-fixed{
  background: rgba(255, 255, 255, 0.95) !important;
  box-shadow: 0 5px 6px -5px rgba(133, 133, 133, 0.6);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}
.v-theme--dark .v-toolbar.nav-fixed{
  background: rgba(18, 18, 18, 0.95) !important;
}
.v-theme--dark .v-toolbar.nav-fixed a {
  color: rgba(255, 255, 255, 0.8) !important;
}
.v-toolbar.nav-fixed .blog-title a,
.v-app-bar.nav-fixed .blog-title a {
  color: #4c4948 !important;
}
.v-toolbar.nav-fixed .menu-btn,
.v-app-bar.nav-fixed .menus-item .menu-btn,
.v-app-bar.nav-fixed .menus-item > a,
.v-app-bar.nav-fixed .nav-title a {
  color: #4c4948 !important;
}
.v-toolbar.nav-fixed .menu-btn:hover,
.v-app-bar.nav-fixed .menus-item .menu-btn:hover,
.v-app-bar.nav-fixed .menus-item > a:hover,
.v-app-bar.nav-fixed .nav-title a:hover {
  color: #49b1f5 !important;
}
.v-toolbar .menus-item {
  position: relative;
  display: inline-block;
  margin: 0 0 0 0.875rem;
}
.v-toolbar .menus-item > a {
  color: #eee !important;
  transition: all 0.2s;
}
.v-toolbar.nav-fixed .menus-item > a,
.v-app-bar.nav-fixed .menus-item > a {
  color: #4c4948 !important;
}
.v-toolbar .menus-item a:after {
  position: absolute;
  bottom: -5px;
  left: 0;
  z-index: -1;
  width: 0;
  height: 3px;
  background-color: #80c8f8;
  content: "";
  transition: all 0.3s ease-in-out;
}
.v-toolbar .menu-btn:hover:after {
  width: 100%;
}
.v-toolbar .user-avatar {
  cursor: pointer;
  border-radius: 50%;
}
/* Dropdown menu styles using v-menu */
.nav-dropdown-list {
  background: #fff !important;
  border-radius: 6px !important;
  padding: 6px 0 !important;
  box-shadow: 0 5px 20px -4px rgba(0, 0, 0, 0.5) !important;
  min-width: 120px !important;
}
.nav-dropdown-item {
  min-height: 36px !important;
  padding: 4px 20px !important;
}
.nav-dropdown-item .v-list-item-title {
  color: #4c4948 !important;
  font-size: 14px !important;
  white-space: nowrap !important;
}
.nav-dropdown-item:hover {
  background: #4ab1f4 !important;
}
.nav-dropdown-item:hover .v-list-item-title {
  color: #fff !important;
}
.v-theme--dark .nav-dropdown-list {
  background: #2c2c2c !important;
}
.v-theme--dark .nav-dropdown-item .v-list-item-title {
  color: rgba(255, 255, 255, 0.85) !important;
}
.v-theme--dark .nav-dropdown-item:hover {
  background: #4ab1f4 !important;
}
.v-theme--dark .nav-dropdown-item:hover .v-list-item-title {
  color: #fff !important;
}
</style>
