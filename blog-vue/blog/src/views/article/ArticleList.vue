<template>
  <div>
    <!-- 标签或分类名 -->
    <div class="banner" :style="cover">
      <h1 class="banner-title animated fadeInDown">{{ title }} - {{ name }}</h1>
    </div>
    <div class="article-list-wrapper">
      <v-row>
        <v-col md="4" cols="12" v-for="item of articleList" :key="item.id">
          <!-- 文章 -->
          <v-card class="animated zoomIn article-item-card">
            <div class="article-item-cover">
              <router-link :to="'/articles/' + item.id">
                <!-- 缩略图 -->
                <v-img
                  class="on-hover"
                  width="100%"
                  height="100%"
                  :src="item.articleCover"
                />
              </router-link>
            </div>
            <div class="article-item-info">
              <!-- 文章标题 -->
              <div>
                <router-link :to="'/articles/' + item.id">
                  {{ item.articleTitle }}
                </router-link>
              </div>
              <div style="margin-top:0.375rem">
                <!-- 发表时间 -->
                <v-icon size="20">mdi-clock-outline</v-icon>
                {{ $filters.date(item.createTime) }}
                <!-- 文章分类 -->
                <router-link
                  :to="'/categories/' + item.categoryId"
                  class="float-right"
                >
                  <v-icon>mdi-bookmark</v-icon>{{ item.categoryName }}
                </router-link>
              </div>
            </div>
            <!-- 分割线 -->
            <v-divider></v-divider>
            <!-- 文章标签 -->
            <div class="tag-wrapper">
              <router-link
                :to="'/tags/' + tag.id"
                class="tag-btn"
                v-for="tag of item.tagDTOList"
                :key="tag.id"
              >
                {{ tag.tagName }}
              </router-link>
            </div>
          </v-card>
        </v-col>
      </v-row>
      <!-- TODO: Vue3 migration - removed vue-infinite-loading dependency -->
      <!-- Load more button replacing infinite-loading -->
      <div v-if="hasMore" class="text-center mt-4">
        <v-btn color="#49b1f5" variant="outlined" @click="loadMore" :loading="loading">
          加载更多
        </v-btn>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute } from "vue-router";
import axios from "axios";
import { useBlogStore } from "@/store";

const store = useBlogStore();
const route = useRoute();

// Refs
const current = ref(1);
const articleList = ref([]);
const name = ref("");
const title = ref("");
const hasMore = ref(true);
const loading = ref(false);

// Computed
const cover = computed(() => {
  var coverUrl = "";
  store.blogInfo.pageList.forEach(item => {
    if (item.pageLabel == "articleList") {
      coverUrl = item.pageCover;
    }
  });
  return "background: url(" + coverUrl + ") center center / cover no-repeat";
});

// Methods
function loadMore() {
  loading.value = true;
  axios
    .get("/api/articles/condition", {
      params: {
        categoryId: route.params.categoryId,
        tagId: route.params.tagId,
        current: current.value
      }
    })
    .then(({ data }) => {
      if (data.data.name) {
        name.value = data.data.name;
        document.title = title.value + " - " + name.value;
      }
      if (data.data.articlePreviewDTOList.length) {
        current.value++;
        articleList.value.push(...data.data.articlePreviewDTOList);
        loading.value = false;
      } else {
        hasMore.value = false;
        loading.value = false;
      }
    });
}

// Lifecycle
onMounted(() => {
  const path = route.path;
  if (path.indexOf("/categories") != -1) {
    title.value = "分类";
  } else {
    title.value = "标签";
  }
  // Initial load
  loadMore();
});
</script>

<style scoped>
@media (min-width: 760px) {
  .article-list-wrapper {
    max-width: 1106px;
    margin: 370px auto 1rem auto;
  }
  .article-item-card:hover {
    transition: all 0.3s;
    box-shadow: 0 4px 12px 12px rgba(7, 17, 27, 0.15);
  }
  .article-item-card:not(:hover) {
    transition: all 0.3s;
  }
  .article-item-card:hover .on-hover {
    transition: all 0.6s;
    transform: scale(1.1);
  }
  .article-item-card:not(:hover) .on-hover {
    transition: all 0.6s;
  }
  .article-item-info {
    line-height: 1.7;
    padding: 15px 15px 12px 18px;
    font-size: 15px;
  }
}
@media (max-width: 759px) {
  .article-list-wrapper {
    margin-top: 230px;
    padding: 0 12px;
  }
  .article-item-info {
    line-height: 1.7;
    padding: 15px 15px 12px 18px;
  }
}
.article-item-card {
  border-radius: 8px !important;
  box-shadow: 0 4px 8px 6px rgba(7, 17, 27, 0.06);
}
.article-item-card a {
  transition: all 0.3s;
}
.article-item-cover {
  height: 220px;
  overflow: hidden;
}
.article-item-card a:hover {
  color: #8e8cd8;
}
.tag-wrapper {
  padding: 10px 15px 10px 18px;
}
.tag-wrapper a {
  color: #fff !important;
}
.tag-btn {
  display: inline-block;
  font-size: 0.725rem;
  line-height: 22px;
  height: 22px;
  border-radius: 10px;
  padding: 0 12px !important;
  background: linear-gradient(to right, #bf4643 0%, #6c9d8f 100%);
  opacity: 0.6;
  margin-right: 0.5rem;
}
</style>
