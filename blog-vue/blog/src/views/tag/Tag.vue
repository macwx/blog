<template>
  <div>
    <!-- banner -->
    <div class="banner" :style="cover">
      <h1 class="banner-title">标签</h1>
    </div>


    <!-- 标签列表 -->
    <v-card class="blog-container">
      <div class="tag-cloud-title">标签 - {{ count }}</div>

      <div class="tag-cloud">
        <svg
            :width="width"
            :height="height"
            @mousemove="listener($event)"
        >
          <router-link
              v-for="(tag, index) in tags"
              :key="tag.id"
              :to="'/tags/' + tag.id"
          >
            <text
                :x="tag.x"
                :y="tag.y"
                :font-size="20 * (600 / (600 - tag.z))"
                :fill-opacity="(400 + tag.z) / 600"
                :fill="colors[index]"
            >
              {{ tag.tagName }}
            </text>
          </router-link>
        </svg>
      </div>


<!--      <div class="tag-cloud">-->
<!--        <router-link-->
<!--          :style="{ 'font-size': Math.floor(Math.random() * 10) + 18 + 'px' }"-->
<!--          v-for="item of tagList"-->
<!--          :key="item.id"-->
<!--          :to="'/tags/' + item.id"-->
<!--        >-->
<!--          {{ item.tagName }}-->
<!--        </router-link>-->
<!--      </div>-->
    </v-card>




  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from "vue";
import axios from "axios";
import { useBlogStore } from "@/store";

const store = useBlogStore();

// Refs
const width = ref(460);
const height = ref(460);
const RADIUS = ref(230);
const speedX = ref(Math.PI / 360);
const speedY = ref(Math.PI / 360);
const tags = ref([]);
const colors = ref([]);
const tagList = ref([]);
const count = ref(0);

// Timer reference for cleanup
let intervalId = null;

// Computed
const CX = computed(() => width.value / 2);
const CY = computed(() => height.value / 2);

const cover = computed(() => {
  var coverUrl = "";
  store.blogInfo.pageList.forEach(item => {
    if (item.pageLabel == "tag") {
      coverUrl = item.pageCover;
    }
  });
  return "background: url(" + coverUrl + ") center center / cover no-repeat";
});

// Methods
function listTags() {
  axios.get("/api/tags").then(({ data }) => {
    tagList.value = data.data.recordList;
    count.value = data.data.count;
  });
}

function rotateX(angleX) {
  var cos = Math.cos(angleX);
  var sin = Math.sin(angleX);
  for (let tag of tags.value) {
    var y1 = (tag.y - CY.value) * cos - tag.z * sin + CY.value;
    var z1 = tag.z * cos + (tag.y - CY.value) * sin;
    tag.y = y1;
    tag.z = z1;
  }
}

function rotateY(angleY) {
  var cos = Math.cos(angleY);
  var sin = Math.sin(angleY);
  for (let tag of tags.value) {
    var x1 = (tag.x - CX.value) * cos - tag.z * sin + CX.value;
    var z1 = tag.z * cos + (tag.x - CX.value) * sin;
    tag.x = x1;
    tag.z = z1;
  }
}

function listener(event) {
  var x = event.clientX - CX.value;
  var y = event.clientY - CY.value;
  speedX.value = x * 0.0001 > 0 ? Math.min(RADIUS.value * 0.00002, x * 0.0001) : Math.max(-RADIUS.value * 0.00002, x * 0.0001);
  speedY.value = y * 0.0001 > 0 ? Math.min(RADIUS.value * 0.00002, y * 0.0001) : Math.max(-RADIUS.value * 0.00002, y * 0.0001);
}

function changeColors() {
  for (var i = 0; i < 30; i++) {
    var r = Math.floor(Math.random() * 256);
    var g = Math.floor(Math.random() * 256);
    var b = Math.floor(Math.random() * 256);
    colors.value[i] = "rgb(" + r + "," + g + "," + b + ")";
  }
}

// Lifecycle
listTags();
changeColors();

// 初始化标签位置
axios.get("/api/tags").then(res => {
  let newTags = [];
  for (let i = 0; i < res.data.data.recordList.length; i++) {
    let tag = {};
    let k = -1 + (2 * (i + 1) - 1) / res.data.data.recordList.length;
    let a = Math.acos(k);
    let b = a * Math.sqrt(res.data.data.recordList.length * Math.PI);
    tag.tagName = res.data.data.recordList[i].tagName;
    tag.id = res.data.data.recordList[i].id;
    tag.x = CX.value + RADIUS.value * Math.sin(a) * Math.cos(b);
    tag.y = CY.value + RADIUS.value * Math.sin(a) * Math.sin(b);
    tag.z = RADIUS.value * Math.cos(a);
    newTags.push(tag);
  }
  tags.value = newTags;
});

onMounted(() => {
  intervalId = setInterval(() => {
    rotateX(speedX.value);
    rotateY(speedY.value);
  }, 17);
});

onUnmounted(() => {
  if (intervalId) {
    clearInterval(intervalId);
  }
});
</script>

<style scoped>
.tag-cloud-title {
  line-height: 2;
  font-size: 36px;
  text-align: center;
}
@media (max-width: 759px) {
  .tag-cloud-title {
    font-size: 25px;
  }
}
.tag-cloud {
  text-align: center;
}
.tag-cloud a {
  color: #616161;
  display: inline-block;
  text-decoration: none;
  padding: 0 8px;
  line-height: 2;
  transition: all 0.3s;
}
.tag-cloud a:hover {
  color: #03a9f4 !important;
  transform: scale(1.1);
}
</style>
