<template>
  <v-app id="app">
    <FloatingImage />
    <TopNavBar />
    <SideNavBar />
    <v-main>
      <router-view :key="$route.fullPath" />
    </v-main>
    <Footer />
    <BackTop />
    <SearchModel />
    <LoginModel />
    <RegisterModel />
    <ForgetModel />
    <EmailModel />
    <Player v-if="blogInfo.websiteConfig && blogInfo.websiteConfig.isMusicPlayer === 1" />
    <ChatRoom v-if="blogInfo.websiteConfig && blogInfo.websiteConfig.isChatRoom === 1" />
  </v-app>
</template>

<script setup>
import { computed, onMounted } from "vue";
import axios from "axios";
import { useBlogStore } from "@/store";
import TopNavBar from "./components/layout/TopNavBar.vue";
import SideNavBar from "./components/layout/SideNavBar.vue";
import Footer from "./components/layout/Footer.vue";
import BackTop from "./components/BackTop.vue";
import SearchModel from "./components/model/SearchModel.vue";
import LoginModel from "./components/model/LoginModel.vue";
import RegisterModel from "./components/model/RegisterModel.vue";
import ForgetModel from "./components/model/ForgetModel.vue";
import EmailModel from "./components/model/EmailModel.vue";
import Player from "./components/zw-player/player.vue";
import ChatRoom from "./components/ChatRoom.vue";
import FloatingImage from "./components/FloatingImage.vue";

const store = useBlogStore();

const blogInfo = computed(() => store.blogInfo);

onMounted(() => {
  axios.get("/api/").then(({ data }) => {
    store.checkBlogInfo(data.data);
  });
  axios.post("/api/report");
});
</script>
