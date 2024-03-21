<template>
  <v-app id="app">
    <!-- 导航栏 -->
    <TopNavBar></TopNavBar>
    <!-- 侧边导航栏 -->
    <SideNavBar></SideNavBar>
    <!-- 内容 -->
    <v-main>
      <router-view :key="$route.fullPath" />
    </v-main>
    <!-- 页脚 -->
    <Footer></Footer>
    <!-- 返回顶部 -->
    <BackTop></BackTop>
    <!-- 搜索模态框 -->
    <searchModel></searchModel>
    <!-- 登录模态框 -->
    <LoginModel></LoginModel>
    <!-- 注册模态框 -->
    <RegisterModel></RegisterModel>
    <!-- 忘记密码模态框 -->
    <ForgetModel></ForgetModel>
    <!-- 绑定邮箱模态框 -->
    <EmailModel></EmailModel>
    <!-- 音乐播放器 -->
    <Player v-if="blogInfo.websiteConfig.isMusicPlayer === 1" />
    <!-- 聊天室 -->
    <ChatRoom v-if="blogInfo.websiteConfig.isChatRoom === 1"></ChatRoom>
  </v-app>
</template>

<script>
import TopNavBar from "./components/layout/TopNavBar";
import SideNavBar from "./components/layout/SideNavBar";
import Footer from "./components/layout/Footer";
import BackTop from "./components/BackTop";
import searchModel from "./components/model/SearchModel";
import LoginModel from "./components/model/LoginModel";
import RegisterModel from "./components/model/RegisterModel";
import ForgetModel from "./components/model/ForgetModel";
import EmailModel from "./components/model/EmailModel";
import Player from "./components/zw-player/player.vue";
import ChatRoom from "./components/ChatRoom";
export default {
  name: 'App',
  /*看板娘初始化
    参数说明
    model 模型的配置
       json 文件资源路径，可以支持网络路径，此处使用的是相对路径
       scale 模型缩放系数
    display 模型布局
       position 位置
       width 宽度
       height 高度
       hOffset 水平偏移量
       vOffset 垂直偏移量
    mobile 移动端配置
       show 显示/隐藏
       scale 缩放比例
    react 模型矩形框样式
       opacity 透明度
       opacityDefault 默认透明度
       opacityOnHover 鼠标悬浮透明度
  */
  created() {
    setTimeout(() => {
      window.L2Dwidget.init({
        pluginRootPath: '../live2dw/',
        pluginJsPath: 'lib/',
        pluginModelPath: 'live2d-widget-model-haru_1/assets/', //中间这个haru_2就是你的老婆,想换个老婆,换这个就可以了
        tagMode: false,
        debug: false,
        model: { jsonPath: '../live2dw/live2d-widget-model-haru_1/assets/haru01.model.json' },
        display: { position: 'left', width: 300, height: 600 },  //调整大小,和位置
        mobile: { show: true },   //要不要盯着你的鼠标看
        log: false
      });
    });

  // 获取博客信息
    this.getBlogInfo();
    // 上传访客信息
    this.axios.post("/api/report");
  },
  components: {
    TopNavBar,
    Player,
    SideNavBar,
    Footer,
    BackTop,
    searchModel,
    LoginModel,
    RegisterModel,
    ForgetModel,
    EmailModel,
    ChatRoom
  },
  methods: {
    getBlogInfo() {
      this.axios.get("/api/").then(({ data }) => {
        console.log(data.data);
        this.$store.commit("checkBlogInfo", data.data);
      });
    }
  },
  computed: {
    blogInfo() {
      return this.$store.state.blogInfo;
    },
    isMobile() {
      const flag = navigator.userAgent.match(
        /(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i
      );
      return flag;
    }
  }
};
</script>
