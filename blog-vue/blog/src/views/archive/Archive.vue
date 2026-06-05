<template>
  <div>
    <!-- banner -->
    <div class="banner" :style="cover">
      <h1 class="banner-title">归档</h1>
    </div>
    <!-- 归档列表 -->
    <v-card class="blog-container">
      <div class="timeline">
        <div class="timeline-title">目前共计{{ count }}篇文章，继续加油</div>
        <div class="timeline-item" v-for="item of archiveList" :key="item.id">
          <div class="timeline-dot"></div>
          <div class="timeline-content">
            <span class="time">{{ $filters.date(item.createTime) }}</span>
            <router-link
              :to="'/articles/' + item.id"
              style="color:#666;text-decoration: none"
            >
              {{ item.articleTitle }}
            </router-link>
          </div>
        </div>
      </div>
      <!-- 分页按钮 -->
      <v-pagination
        color="#00C4B6"
        v-model="current"
        :length="Math.ceil(count / 10)"
        total-visible="7"
      />
    </v-card>
  </div>
</template>

<script setup>
import { ref, computed, watch } from "vue";
import axios from "axios";
import { useBlogStore } from "@/store";

const store = useBlogStore();

const current = ref(1);
const count = ref(0);
const archiveList = ref([]);

const cover = computed(() => {
  var coverUrl = "";
  store.blogInfo.pageList.forEach(item => {
    if (item.pageLabel == "archive") {
      coverUrl = item.pageCover;
    }
  });
  return "background: url(" + coverUrl + ") center center / cover no-repeat";
});

function listArchives() {
  axios
    .get("/api/articles/archives", {
      params: { current: current.value }
    })
    .then(({ data }) => {
      archiveList.value = data.data.recordList;
      count.value = data.data.count;
    });
}

watch(current, (value) => {
  axios
    .get("/api/articles/archives", {
      params: { current: value }
    })
    .then(({ data }) => {
      archiveList.value = data.data.recordList;
      count.value = data.data.count;
    });
});

listArchives();
</script>

<style scoped>
.timeline {
  padding: 20px 0 20px 40px;
  position: relative;
}
.timeline::before {
  content: "";
  position: absolute;
  left: 20px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: #e0e0e0;
}
.timeline-title {
  font-size: 1rem;
  color: #555;
  margin-bottom: 20px;
  position: relative;
}
.timeline-item {
  position: relative;
  margin-bottom: 20px;
  display: flex;
  align-items: flex-start;
}
.timeline-dot {
  position: absolute;
  left: -26px;
  top: 6px;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #00C4B6;
}
.timeline-content {
  display: flex;
  align-items: center;
  gap: 10px;
}
.time {
  font-size: 0.75rem;
  color: #555;
  white-space: nowrap;
}
</style>
