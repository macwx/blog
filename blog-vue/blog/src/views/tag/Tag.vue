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

<script>
export default {
  created() {
    this.listTags();
    this.changeColors();
    //初始化标签位置
    let tags = [];
    this.axios.get("/api/tags").then(res => {
      console.log(res.data);
      console.log(res.data.data);
      for (let i = 0; i < res.data.data.recordList.length; i++) {
        console.log(res.data.data.recordList[i]);
        let tag = {};
        let k = -1 + (2 * (i + 1) - 1) / res.data.data.recordList.length;
        let a = Math.acos(k);
        let b = a * Math.sqrt(res.data.data.recordList.length * Math.PI);
        tag.tagName = res.data.data.recordList[i].tagName;
        tag.x = this.CX + this.RADIUS * Math.sin(a) * Math.cos(b);
        tag.y = this.CY + this.RADIUS * Math.sin(a) * Math.sin(b);
        tag.z = this.RADIUS * Math.cos(a);
        tags.push(tag);
      }
    });
    console.log(tags);
    this.tags = tags;
  },
  data: function() {
    return {
      width: 460, //svg宽度
      height: 460, //svg高度
      RADIUS: 230, //球的半径
      speedX: Math.PI / 360, //球一帧绕x轴旋转的角度
      speedY: Math.PI / 360, //球-帧绕y轴旋转的角度
      tags: [],
      colors: [], //存储颜色
      tagList: [],
      count: 0
    };
  },
  methods: {
    listTags() {
      this.axios.get("/api/tags").then(({ data }) => {
        this.tagList = data.data.recordList;
        this.count = data.data.count;
      });
    },
    rotateX(angleX) {
      var cos = Math.cos(angleX);
      var sin = Math.sin(angleX);
      for (let tag of this.tags) {
        var y1 = (tag.y - this.CY) * cos - tag.z * sin + this.CY;
        var z1 = tag.z * cos + (tag.y - this.CY) * sin;
        tag.y = y1;
        tag.z = z1;
      }
    },
    rotateY(angleY) {
      var cos = Math.cos(angleY);
      var sin = Math.sin(angleY);
      for (let tag of this.this.tags) {
        var x1 = (tag.x - this.CX) * cos - tag.z * sin + this.CX;
        var z1 = tag.z * cos + (tag.x - this.CX) * sin;
        tag.x = x1;
        tag.z = z1;
      }
    },
    listener(event) {//响应鼠标移动
      var x = event.clientX - this.CX;
      var y = event.clientY - this.CY;
      this.speedX = x * 0.0001 > 0 ? Math.min(this.RADIUS * 0.00002, x * 0.0001) : Math.max(-this.RADIUS * 0.00002, x * 0.0001);
      this.speedY = y * 0.0001 > 0 ? Math.min(this.RADIUS * 0.00002, y * 0.0001) : Math.max(-this.RADIUS * 0.00002, y * 0.0001);
    },
    changeColors() { //随机变色
      for (var i = 0; i < 30; i++) {
        var r = Math.floor(Math.random() * 256);
        var g = Math.floor(Math.random() * 256);
        var b = Math.floor(Math.random() * 256);
        this.colors[i] = "rgb(" + r + "," + g + "," + b + ")";
      }
    }
  },
  mounted: function() {
    setInterval(() => {
      this.rotateX(this.speedX);
      this.rotateY(this.speedY);
    }, 17);
  },
  computed: {
    CX() {
      return this.width / 2;
    },
    CY() {
      return this.height / 2;
    },
    cover() {
      var cover = "";
      this.$store.state.blogInfo.pageList.forEach(item => {
        if (item.pageLabel == "tag") {
          cover = item.pageCover;
        }
      });
      return "background: url(" + cover + ") center center / cover no-repeat";
    }
  }
};
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
