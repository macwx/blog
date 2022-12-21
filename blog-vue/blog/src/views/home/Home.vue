<template>
  <div>
    <!-- banner -->
    <div class="home-banner" :style="cover">
      <div class="banner-container">
        <!-- 联系方式 -->
        <h1 class="blog-title animated zoomIn">
          {{ blogInfo.websiteConfig.websiteName }}
        </h1>
        <!-- 一言 -->
        <div class="blog-intro">
          {{ obj.output }} <span class="typed-cursor">|</span>
        </div>
        <!-- 联系方式 -->
        <div class="blog-contact">
          <a
            v-if="isShowSocial('qq')"
            class="mr-5 iconfont iconqq"
            target="_blank"
            :href="
              'http://wpa.qq.com/msgrd?v=3&uin=' +
                blogInfo.websiteConfig.qq +
                '&site=qq&menu=yes'
            "
          />
          <a
            v-if="isShowSocial('github')"
            target="_blank"
            :href="blogInfo.websiteConfig.github"
            class="mr-5 iconfont icongithub"
          />
          <a
            v-if="isShowSocial('gitee')"
            target="_blank"
            :href="blogInfo.websiteConfig.gitee"
            class="iconfont icongitee-fill-round"
          />
        </div>
      </div>
      <!-- 向下滚动 -->
      <div class="scroll-down" @click="scrollDown">
        <v-icon color="#fff" class="scroll-down-effects">
          mdi-chevron-down
        </v-icon>
      </div>
    </div>
    <!-- 主页文章 -->
    <v-row class="home-container">
      <v-col md="9" cols="12">
        <!-- 说说轮播 -->
        <v-card class="animated zoomIn" v-if="talkList.length > 0">
          <Swiper :list="talkList" />
        </v-card>
        <v-card
          class="animated zoomIn article-card"
          style="border-radius: 12px 8px 8px 12px"
          v-for="(item, index) of articleList"
          :key="item.id"
        >
          <!-- 文章封面图 -->
          <div :class="isRight(index)">
            <router-link :to="'/articles/' + item.id">
              <v-img
                class="on-hover"
                width="100%"
                height="100%"
                :src="item.articleCover"
              />
            </router-link>
          </div>
          <!-- 文章信息 -->
          <div class="article-wrapper">
            <div style="line-height:1.4">
              <router-link :to="'/articles/' + item.id">
                {{ item.articleTitle }}
              </router-link>
            </div>
            <div class="article-info">
              <!-- 是否置顶 -->
              <span v-if="item.isTop == 1">
                <span style="color:#ff7242">
                  <i class="iconfont iconzhiding" /> 置顶
                </span>
                <span class="separator">|</span>
              </span>
              <!-- 发表时间 -->
              <v-icon size="14">mdi-calendar-month-outline</v-icon>
              {{ item.createTime | date }}
              <span class="separator">|</span>
              <!-- 文章分类 -->
              <router-link :to="'/categories/' + item.categoryId">
                <v-icon size="14">mdi-inbox-full</v-icon>
                {{ item.categoryName }}
              </router-link>
              <span class="separator">|</span>
              <!-- 文章标签 -->
              <router-link
                style="display:inline-block"
                :to="'/tags/' + tag.id"
                class="mr-1"
                v-for="tag of item.tagDTOList"
                :key="tag.id"
              >
                <v-icon size="14">mdi-tag-multiple</v-icon>
                {{ tag.tagName }}
              </router-link>
            </div>
            <!-- 文章内容 -->
            <div class="article-content">
              {{ item.articleContent }}
            </div>
          </div>
        </v-card>
        <!-- 无限加载 -->
        <infinite-loading @infinite="infiniteHandler">
          <div slot="no-more" />
        </infinite-loading>
      </v-col>
      <!-- 博主信息 -->
      <v-col md="3" cols="12" class="d-md-block d-none">
        <div class="blog-wrapper">
          <v-card class="animated zoomIn blog-card mt-5">
            <div class="author-wrapper">
              <!-- 博主头像 -->
              <v-avatar size="110">
                <img
                  class="author-avatar"
                  :src="blogInfo.websiteConfig.websiteAvatar"
                />
              </v-avatar>
              <div style="font-size: 1.375rem;margin-top:0.625rem">
                {{ blogInfo.websiteConfig.websiteAuthor }}
              </div>
              <div style="font-size: 0.875rem;">
                {{ blogInfo.websiteConfig.websiteIntro }}
              </div>
            </div>
            <!-- 博客信息 -->
            <div class="blog-info-wrapper">
              <div class="blog-info-data">
                <router-link to="/archives">
                  <div style="font-size: 0.875rem">文章</div>
                  <div style="font-size: 1.25rem">
                    {{ blogInfo.articleCount }}
                  </div>
                </router-link>
              </div>
              <div class="blog-info-data">
                <router-link to="/categories">
                  <div style="font-size: 0.875rem">分类</div>
                  <div style="font-size: 1.25rem">
                    {{ blogInfo.categoryCount }}
                  </div>
                </router-link>
              </div>
              <div class="blog-info-data">
                <router-link to="/tags">
                  <div style="font-size: 0.875rem">标签</div>
                  <div style="font-size: 1.25rem">{{ blogInfo.tagCount }}</div>
                </router-link>
              </div>
            </div>
            <!-- 收藏按钮 -->
            <a class="collection-btn" @click="tip = true">
              <v-icon color="#fff" size="18" class="mr-1">mdi-bookmark</v-icon>
              加入书签
            </a>
            <!-- 社交信息 -->
<!--            <div class="card-info-social">-->
<!--              <a-->
<!--                v-if="isShowSocial('qq')"-->
<!--                class="mr-5 iconfont iconqq"-->
<!--                target="_blank"-->
<!--                :href="-->
<!--                  'http://wpa.qq.com/msgrd?v=3&uin=' +-->
<!--                    blogInfo.websiteConfig.qq +-->
<!--                    '&site=qq&menu=yes'-->
<!--                "-->
<!--              />-->
<!--              <a-->
<!--                v-if="isShowSocial('github')"-->
<!--                target="_blank"-->
<!--                :href="blogInfo.websiteConfig.github"-->
<!--                class="mr-5 iconfont icongithub"-->
<!--              />-->
<!--              <a-->
<!--                v-if="isShowSocial('gitee')"-->
<!--                target="_blank"-->
<!--                :href="blogInfo.websiteConfig.gitee"-->
<!--                class="iconfont icongitee-fill-round"-->
<!--              />-->
<!--            </div>-->
          </v-card>

          <!-- 公告 -->
          <v-card class="blog-card animated zoomIn mt-5 big">
            <div class="web-info-title">
              <v-icon size="18">mdi-bell</v-icon>
              公告
            </div>
            <div style="font-size:0.875rem">
              {{ blogInfo.websiteConfig.websiteNotice }}
            </div>
          </v-card>
          <!--关注我们-->
          <v-card class="blog-card animated zoomIn mt-5 big">
            <div class="web-info-title">
              <v-icon size="18">mdi-account</v-icon>
              源码地址
            </div>
            <div class="guanzhu" id="follow-us" ref="follow">
              <ul>
                <li v-if="isShowSocial('gitee')" class="gitee">
                  <a :href="blogInfo.websiteConfig.gitee" target="_blank">{{
                      blogInfo.websiteConfig.gitee
                    }}</a>
                </li>

                <li v-if="isShowSocial('email')" class="email">
                  <a href="javascript:void(0);">{{
                    blogInfo.websiteConfig.email
                  }}</a>
                </li>
                <li v-if="isShowSocial('github')" class="github">
                  <a :href="blogInfo.websiteConfig.github" target="_blank">{{
                    blogInfo.websiteConfig.github
                  }}</a>
                </li>
                <li v-if="isShowSocial('qq')" class="qq">
                  <a
                      :href="
                      'tencent://AddContact/?fromId=50&fromSubId=1&subcmd=all&uin=' +
                        blogInfo.websiteConfig.qq
                    "
                      target="_blank"
                  >{{ blogInfo.websiteConfig.qq }}</a
                  >
                </li>
<!--                 <li class="wx"><img src="../../../static/images/wx.jpg"></li>-->
              </ul>
            </div>
          </v-card>

          <!--天气-->
          <v-card class="blog-card animated zoomIn mt-5">
            <div id="he-plugin-standard"></div>
          </v-card>
          <!--标签云已移植到标签页-->

          <!-- 网站信息 -->
          <v-card class="blog-card animated zoomIn mt-5">
            <div class="web-info-title">
              <v-icon size="18">mdi-chart-line</v-icon>
              网站资讯
            </div>
            <div class="web-info">
              <div style="padding:4px 0 0">
                网站作者:<span class="float-right">{{ blogInfo.websiteConfig.websiteAuthor }}</span>
              </div>
              <div style="padding:4px 0 0">
                运行时间:<span class="float-right">{{ time }}</span>
              </div>
              <div style="padding:4px 0 0">
                总访问量:<span class="float-right">
                  {{ blogInfo.viewsCount }}
                </span>
              </div>
            </div>
          </v-card>

          <!-- 摸鱼日历 -->
          <v-card class="mt-5" v-click-outside>
            <v-img :src="moyuPic" @click="preview()" />
          </v-card>
        </div>
      </v-col>
    </v-row>
    <!-- 提示消息 -->
    <v-snackbar v-model="tip" top color="#49b1f5" :timeout="2000">
      按CTRL+D 键将本页加入书签
    </v-snackbar>
  </div>
</template>

<script>
import Swiper from "../../components/Swiper.vue";
import EasyTyper from "easy-typer-js";

export default {
  components: {
    Swiper
  },
  created() {
    this.init();
    this.listHomeTalks();
    this.timer = setInterval(this.runTime, 1000);
    this.initMoyuPic();

  },
  data: function() {
    return {
      tip: false,
      time: "",
      obj: {
        output: "",
        isEnd: false,
        speed: 300,
        singleBack: false,
        sleep: 0,
        type: "rollback",
        backSpeed: 40,
        sentencePause: true
      },
      articleList: [],
      moyuPic: "",
      talkList: [],
      current: 1
    };
  },

  methods: {
    // 初始化
    init() {
      document.title = this.blogInfo.websiteConfig.websiteName;
      // 一言Api进行打字机循环输出效果
      fetch("https://v1.hitokoto.cn?c=i")
        .then(res => {
          return res.json();
        })
        .then(({ hitokoto }) => {
          this.initTyped(hitokoto);
        });
    },
    listHomeTalks() {
      this.axios.get("/api/home/talks").then(({ data }) => {
        this.talkList = data.data;
      });
    },
    initTyped(input, fn, hooks) {
      const obj = this.obj;
      // eslint-disable-next-line no-unused-vars
      const typed = new EasyTyper(obj, input, fn, hooks);
    },
    scrollDown() {
      window.scrollTo({
        behavior: "smooth",
        top: document.documentElement.clientHeight
      });
    },
    runTime() {
      var timeold =
        new Date().getTime() -
        new Date(this.blogInfo.websiteConfig.websiteCreateTime).getTime();
      var msPerDay = 24 * 60 * 60 * 1000;
      var daysold = Math.floor(timeold / msPerDay);
      var str = "";
      var day = new Date();
      str += daysold + "天";
      str += day.getHours() + "时";
      str += day.getMinutes() + "分";
      str += day.getSeconds() + "秒";
      this.time = str;
    },
    initMoyuPic() {
      this.axios
        .get("https://api.vvhan.com/api/moyu", {
          params: {
            type: "json"
          }
        })
        .then(({ data }) => {
          this.moyuPic = data.url;
        });
    },
    preview() {
      let pic_images = [this.moyuPic];
      this.$imagePreview({
        showIndex: true,
        loop: false,
        images: pic_images,
        index: 0
      });
    },
    infiniteHandler($state) {
      let md = require("markdown-it")();
      this.axios
        .get("/api/articles", {
          params: {
            current: this.current
          }
        })
        .then(({ data }) => {
          if (data.data.length) {
            // 去除markdown标签
            data.data.forEach(item => {
              item.articleContent = md
                .render(item.articleContent)
                .replace(/<\/?[^>]*>/g, "")
                .replace(/[|]*\n/, "")
                .replace(/&npsp;/gi, "");
            });
            this.articleList.push(...data.data);
            this.current++;
            $state.loaded();
          } else {
            $state.complete();
          }
        });
    }

  },
  metaInfo: {
    meta: [{
      name: "keyWords",
      content: "超伟博客,开源博客,www.macw.cc"  //变量或字符串
    }, {
      name: "description",
      content: "一个专注于技术分享的博客平台,大家以共同学习,乐于分享,拥抱开源的价值观进行学习交流"
    }]
  },
  mounted: function() {
    setInterval(() => {
      this.rotateX(this.speedX);
      this.rotateY(this.speedY);
    }, 17);
    window.WIDGET = {
      CONFIG: {
        layout: "2",
        width: 230,
        height: 300,
        background: "2",
        dataColor: "000000",
        borderRadius: "5",
        city: "CN101180101",
        key: "4034255d0cc94897b5f745367ccb81f2"
      }
    };
    let script = document.createElement("script");
    script.type = "text/javascript";
    script.src =
      "https://widget.qweather.net/standard/static/js/he-standard-common.js?v=2.0";
    document.getElementsByTagName("head")[0].appendChild(script);
  },
  computed: {
    isRight() {
      return function(index) {
        if (index % 2 == 0) {
          return "article-cover left-radius";
        }
        return "article-cover right-radius";
      };
    },
    blogInfo() {
      return this.$store.state.blogInfo;
    },
    isShowSocial() {
      return function(social) {
        return this.blogInfo.websiteConfig.socialUrlList.indexOf(social) != -1;
      };
    },
    cover() {
      var cover = "";
      this.$store.state.blogInfo.pageList.forEach(item => {
        if (item.pageLabel == "home") {
          cover = item.pageCover;
        }
      });
      return "background: url(" + cover + ") center center / cover no-repeat";
    }
  }
};
</script>

<style lang="stylus">
.typed-cursor
  opacity: 1
  animation: blink 0.7s infinite

@keyframes blink
  0%
    opacity: 1
  50%
    opacity: 0
  100%
    opacity: 1
</style>

<style scoped>
.home-banner {
  position: absolute;
  top: -60px;
  left: 0;
  right: 0;
  height: 100vh;
  background-attachment: fixed;
  text-align: center;
  color: #fff !important;
  animation: header-effect 1s;
}

.banner-container {
  margin-top: 43vh;
  line-height: 1.5;
  color: #eee;
}

.blog-contact a {
  color: #fff !important;
}

.card-info-social {
  line-height: 40px;
  text-align: center;
  margin: 6px 0 -6px;
}

.card-info-social a {
  font-size: 1.5rem;
}

.left-radius {
  border-radius: 8px 0 0 8px !important;
  order: 0;
}

.right-radius {
  border-radius: 0 8px 8px 0 !important;
  order: 1;
}

.article-wrapper {
  font-size: 14px;
}

@media (min-width: 760px) {
  .blog-title {
    font-size: 2.5rem;
  }

  .blog-intro {
    font-size: 1.5rem;
  }

  .blog-contact {
    display: none;
  }

  .home-container {
    max-width: 1200px;
    margin: calc(100vh - 48px) auto 28px auto;
    padding: 0 5px;
  }

  .article-card {
    display: flex;
    align-items: center;
    height: 280px;
    width: 100%;
    margin-top: 20px;
  }

  .article-cover {
    overflow: hidden;
    height: 100%;
    width: 45%;
  }

  .on-hover {
    transition: all 0.6s;
  }

  .article-card:hover .on-hover {
    transform: scale(1.1);
  }

  .article-wrapper {
    padding: 0 2.5rem;
    width: 55%;
  }

  .article-wrapper a {
    font-size: 1.5rem;
    transition: all 0.3s;
  }
}

@media (max-width: 759px) {
  .blog-title {
    font-size: 26px;
  }

  .blog-contact {
    font-size: 1.25rem;
    line-height: 2;
  }

  .home-container {
    width: 100%;
    margin: calc(100vh - 66px) auto 0 auto;
  }

  .article-card {
    margin-top: 1rem;
  }

  .article-cover {
    border-radius: 8px 8px 0 0 !important;
    height: 230px !important;
    width: 100%;
  }

  .article-cover div {
    border-radius: 8px 8px 0 0 !important;
  }

  .article-wrapper {
    padding: 1.25rem 1.25rem 1.875rem;
  }

  .article-wrapper a {
    font-size: 1.25rem;
    transition: all 0.3s;
  }
}

.scroll-down {
  cursor: pointer;
  position: absolute;
  bottom: 0;
  width: 100%;
}

.scroll-down i {
  font-size: 2rem;
}

.article-wrapper a:hover {
  color: #8e8cd8;
}

.article-info {
  font-size: 95%;
  color: #858585;
  line-height: 2;
  margin: 0.375rem 0;
}

.article-info a {
  font-size: 95%;
  color: #858585 !important;
}

.article-content {
  line-height: 2;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.blog-wrapper {
  position: sticky;
  top: 10px;
}

.guanzhu {
  margin-top: 10px;
}

.guanzhu .qqGroup {
  background: url(data:image/png;base64,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)
    no-repeat;
  background-size: 22px;
  background-position-y: 47%;
}

.guanzhu .qq {
  background: url(data:image/jpeg;base64,/9j/4QAYRXhpZgAASUkqAAgAAAAAAAAAAAAAAP/sABFEdWNreQABAAQAAABkAAD/4QMraHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wLwA8P3hwYWNrZXQgYmVnaW49Iu+7vyIgaWQ9Ilc1TTBNcENlaGlIenJlU3pOVGN6a2M5ZCI/PiA8eDp4bXBtZXRhIHhtbG5zOng9ImFkb2JlOm5zOm1ldGEvIiB4OnhtcHRrPSJBZG9iZSBYTVAgQ29yZSA1LjMtYzAxMSA2Ni4xNDU2NjEsIDIwMTIvMDIvMDYtMTQ6NTY6MjcgICAgICAgICI+IDxyZGY6UkRGIHhtbG5zOnJkZj0iaHR0cDovL3d3dy53My5vcmcvMTk5OS8wMi8yMi1yZGYtc3ludGF4LW5zIyI+IDxyZGY6RGVzY3JpcHRpb24gcmRmOmFib3V0PSIiIHhtbG5zOnhtcD0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wLyIgeG1sbnM6eG1wTU09Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8iIHhtbG5zOnN0UmVmPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvc1R5cGUvUmVzb3VyY2VSZWYjIiB4bXA6Q3JlYXRvclRvb2w9IkFkb2JlIFBob3Rvc2hvcCBDUzYgKFdpbmRvd3MpIiB4bXBNTTpJbnN0YW5jZUlEPSJ4bXAuaWlkOkFEMERGOUFCNTcxQzExRTg5OUE4RUY5REMwRkE5Q0E3IiB4bXBNTTpEb2N1bWVudElEPSJ4bXAuZGlkOkFEMERGOUFDNTcxQzExRTg5OUE4RUY5REMwRkE5Q0E3Ij4gPHhtcE1NOkRlcml2ZWRGcm9tIHN0UmVmOmluc3RhbmNlSUQ9InhtcC5paWQ6QUQwREY5QTk1NzFDMTFFODk5QThFRjlEQzBGQTlDQTciIHN0UmVmOmRvY3VtZW50SUQ9InhtcC5kaWQ6QUQwREY5QUE1NzFDMTFFODk5QThFRjlEQzBGQTlDQTciLz4gPC9yZGY6RGVzY3JpcHRpb24+IDwvcmRmOlJERj4gPC94OnhtcG1ldGE+IDw/eHBhY2tldCBlbmQ9InIiPz7/7gAOQWRvYmUAZMAAAAAB/9sAhAABAQEBAQEBAQEBAQEBAQEBAQEB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)
    no-repeat 0 -98px;
}

.guanzhu .email {
  background: url(data:image/jpeg;base64,/9j/4QAYRXhpZgAASUkqAAgAAAAAAAAAAAAAAP/sABFEdWNreQABAAQAAABkAAD/4QMraHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wLwA8P3hwYWNrZXQgYmVnaW49Iu+7vyIgaWQ9Ilc1TTBNcENlaGlIenJlU3pOVGN6a2M5ZCI/PiA8eDp4bXBtZXRhIHhtbG5zOng9ImFkb2JlOm5zOm1ldGEvIiB4OnhtcHRrPSJBZG9iZSBYTVAgQ29yZSA1LjMtYzAxMSA2Ni4xNDU2NjEsIDIwMTIvMDIvMDYtMTQ6NTY6MjcgICAgICAgICI+IDxyZGY6UkRGIHhtbG5zOnJkZj0iaHR0cDovL3d3dy53My5vcmcvMTk5OS8wMi8yMi1yZGYtc3ludGF4LW5zIyI+IDxyZGY6RGVzY3JpcHRpb24gcmRmOmFib3V0PSIiIHhtbG5zOnhtcD0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wLyIgeG1sbnM6eG1wTU09Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8iIHhtbG5zOnN0UmVmPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvc1R5cGUvUmVzb3VyY2VSZWYjIiB4bXA6Q3JlYXRvclRvb2w9IkFkb2JlIFBob3Rvc2hvcCBDUzYgKFdpbmRvd3MpIiB4bXBNTTpJbnN0YW5jZUlEPSJ4bXAuaWlkOkFEMERGOUFCNTcxQzExRTg5OUE4RUY5REMwRkE5Q0E3IiB4bXBNTTpEb2N1bWVudElEPSJ4bXAuZGlkOkFEMERGOUFDNTcxQzExRTg5OUE4RUY5REMwRkE5Q0E3Ij4gPHhtcE1NOkRlcml2ZWRGcm9tIHN0UmVmOmluc3RhbmNlSUQ9InhtcC5paWQ6QUQwREY5QTk1NzFDMTFFODk5QThFRjlEQzBGQTlDQTciIHN0UmVmOmRvY3VtZW50SUQ9InhtcC5kaWQ6QUQwREY5QUE1NzFDMTFFODk5QThFRjlEQzBGQTlDQTciLz4gPC9yZGY6RGVzY3JpcHRpb24+IDwvcmRmOlJERj4gPC94OnhtcG1ldGE+IDw/eHBhY2tldCBlbmQ9InIiPz7/7gAOQWRvYmUAZMAAAAAB/9sAhAABAQEBAQEBAQEBAQEBAQEBAQEB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)
    no-repeat 0 -150px;
}

.guanzhu .github {
  background: url(github-fill.png) no-repeat;
  background-size: 25px;
  background-position-y: 47%;
}

.guanzhu .gitee {
  background: url(data:image/jpeg;base64,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)
    no-repeat;
  background-size: 25px;
  background-position-y: 47%;
}

.guanzhu ul li {
  font-size: 12px;
  margin-bottom: 10px;
  background: #fff;
  color: #525252;
  line-height: 40px;
  padding: 0 0 0 34px;
  border: 1px solid #ddd;
  border-radius: 2px;
  position: relative;
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
}

.blog-card {
  line-height: 2;
  padding: 1.25rem 1.5rem;
}

.author-wrapper {
  text-align: center;
}

.blog-info-wrapper {
  display: flex;
  justify-self: center;
  padding: 0.875rem 0;
}

.blog-info-data {
  flex: 1;
  text-align: center;
}

.blog-info-data a {
  text-decoration: none;
}

.collection-btn {
  text-align: center;
  z-index: 1;
  font-size: 14px;
  position: relative;
  display: block;
  background-color: #49b1f5;
  color: #fff !important;
  height: 32px;
  line-height: 32px;
  transition-duration: 1s;
  transition-property: color;
}

.collection-btn:before {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: -1;
  background: #ff7242;
  content: "";
  transition-timing-function: ease-out;
  transition-duration: 0.5s;
  transition-property: transform;
  transform: scaleX(0);
  transform-origin: 0 50%;
}

.collection-btn:hover:before {
  transition-timing-function: cubic-bezier(0.45, 1.64, 0.47, 0.66);
  transform: scaleX(1);
}

.author-avatar {
  transition: all 0.5s;
}

.author-avatar:hover {
  transform: rotate(360deg);
}

.web-info {
  padding: 0.25rem;
  font-size: 0.875rem;
}

.scroll-down-effects {
  color: #eee !important;
  text-align: center;
  text-shadow: 0.1rem 0.1rem 0.2rem rgba(0, 0, 0, 0.15);
  line-height: 1.5;
  display: inline-block;
  text-rendering: auto;
  -webkit-font-smoothing: antialiased;
  animation: scroll-down-effect 1.5s infinite;
}

@keyframes scroll-down-effect {
  0% {
    top: 0;
    opacity: 0.4;
    filter: alpha(opacity=40);
  }
  50% {
    top: -16px;
    opacity: 1;
    filter: none;
  }
  100% {
    top: 0;
    opacity: 0.4;
    filter: alpha(opacity=40);
  }
}

.big i {
  color: #f00;
  animation: big 0.8s linear infinite;
}

@keyframes big {
  0%,
  100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.2);
  }
}
</style>
