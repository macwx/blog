<template>
  <v-footer app padless absolute v-if="!isMessage">
    <div class="footer-wrap">
      <div>{{ english }}</div>
      <div>{{ chinese }}</div>
      <div>
        ©{{ $filters.year(blogInfo.websiteConfig.websiteCreateTime) }} -
        {{ new Date().getFullYear() }} By
        {{ blogInfo.websiteConfig.websiteAuthor }}

      <a href="https://beian.miit.gov.cn/" target="_blank">
        {{ blogInfo.websiteConfig.websiteRecordNo }}
      </a>
      </div>

    </div>
  </v-footer>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute } from "vue-router";
import { useBlogStore } from "@/store";

const store = useBlogStore();
const route = useRoute();

const chinese = ref("");
const english = ref("");

const isMessage = computed(() => route.path === "/message");
const blogInfo = computed(() => store.blogInfo);

onMounted(() => {
  // 每日一句已关闭（api.vvhan.com 不可用）
});
</script>

<style scoped>
.footer-wrap {
  width: 100%;
  margin-top: 10px;
  line-height: 1.5;
  position: relative;
  padding: 20px 20px;
  color: #eee;
  font-size: 14px;
  text-align: center;
  background: linear-gradient(-45deg, #ee7752, #ce3e75, #23a6d5, #23d5ab);
  background-size: 400% 400%;
  animation: Gradient 10s ease infinite;
}
.footer-wrap a {
  color: #eee !important;
}
@keyframes Gradient {
  0% {
    background-position: 0 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0 50%;
  }
}
</style>
