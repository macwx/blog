<template>
  <div>
    <!-- banner -->
    <div class="banner" :style="cover">
      <h1 class="banner-title">{{ photoAlbumName }}</h1>
    </div>
    <!-- 相册列表 -->
    <v-card class="blog-container">
      <div class="photo-wrap">
        <img
          v-for="(item, index) of photoList"
          class="photo"
          :key="index"
          :src="item"
          @click="preview(index)"
        />
      </div>
      <!-- TODO: Vue3 migration - vue-infinite-loading removed, using scroll listener -->
      <div
        ref="scrollSentinel"
        style="height: 1px"
        v-if="hasMore"
      />
    </v-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from "vue";
import { useRoute } from "vue-router";
import axios from "axios";

const route = useRoute();

const photoAlbumName = ref("");
const photoAlbumCover = ref("");
const photoList = ref([]);
const current = ref(1);
const size = ref(10);
const hasMore = ref(true);
const scrollSentinel = ref(null);
let observer = null;

function preview(index) {
  // TODO: Vue3 migration - $imagePreview removed, use basic preview
  window.open(photoList.value[index], "_blank");
}

function loadPhotos() {
  axios
    .get("/api/albums/" + route.params.albumId + "/photos", {
      params: {
        current: current.value,
        size: size.value
      }
    })
    .then(({ data }) => {
      photoAlbumCover.value = data.data.photoAlbumCover;
      photoAlbumName.value = data.data.photoAlbumName;
      if (data.data.photoList.length) {
        current.value++;
        photoList.value.push(...data.data.photoList);
      } else {
        hasMore.value = false;
      }
    });
}

const cover = computed(() => {
  return (
    "background: url(" +
    photoAlbumCover.value +
    ") center center / cover no-repeat"
  );
});

onMounted(() => {
  loadPhotos();
  // Set up IntersectionObserver for infinite scroll
  observer = new IntersectionObserver((entries) => {
    if (entries[0].isIntersecting && hasMore.value) {
      loadPhotos();
    }
  });
  if (scrollSentinel.value) {
    observer.observe(scrollSentinel.value);
  }
});

onUnmounted(() => {
  if (observer) {
    observer.disconnect();
  }
});
</script>

<style scoped>
.photo-wrap {
  display: flex;
  flex-wrap: wrap;
}
.photo {
  margin: 3px;
  cursor: pointer;
  flex-grow: 1;
  object-fit: cover;
  height: 200px;
}
.photo-wrap::after {
  content: "";
  display: block;
  flex-grow: 9999;
}
@media (max-width: 759px) {
  .photo {
    width: 100%;
  }
}
</style>
