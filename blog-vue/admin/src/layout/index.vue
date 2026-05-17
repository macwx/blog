<template>
  <el-container>
    <!-- 移动端遮罩 -->
    <div
      v-if="isMobile && !$store.state.collapse"
      class="sidebar-overlay"
      @click="$store.commit('trigger')"
    />
    <!-- 侧边栏 -->
    <el-aside width="auto" :class="{ 'mobile-sidebar': isMobile }">
      <SideBar />
    </el-aside>
    <el-container :class="'main-container ' + isHide">
      <!-- 导航栏 -->
      <el-header height="84px" style="padding:0">
        <NavBar :key="$route.fullPath" />
      </el-header>
      <!-- 内容 -->
      <el-main style="background:#F7F9FB">
        <div class="fade-transform-box">
          <transition name="fade-transform" mode="out-in">
            <router-view :key="$route.fullPath" />
          </transition>
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import NavBar from "./components/NavBar";
import SideBar from "./components/SideBar";
export default {
  components: {
    NavBar,
    SideBar
  },
  data() {
    return {
      isMobile: false
    };
  },
  created() {
    this.checkMobile();
    window.addEventListener("resize", this.checkMobile);
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.checkMobile);
  },
  methods: {
    checkMobile() {
      this.isMobile = document.documentElement.clientWidth < 768;
      if (this.isMobile && !this.$store.state.collapse) {
        this.$store.commit("trigger");
      }
    }
  },
  computed: {
    isHide() {
      return this.$store.state.collapse ? "hideSideBar" : "";
    }
  }
};
</script>

<style scoped>
.main-container {
  transition: margin-left 0.45s;
  margin-left: 210px;
  min-height: 100vh;
}
.hideSideBar {
  margin-left: 64px;
}
.sidebar-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.3);
  z-index: 999;
}
.mobile-sidebar {
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1000;
  height: 100%;
}
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.5s ease 0s;
}
.fade-transform-enter {
  opacity: 0;
  transform: translateX(-30px);
}
.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
.fade-transform-box {
  position: relative;
  top: 0px;
  bottom: 0px;
  width: 100%;
  overflow: hidden;
}
@media (max-width: 767px) {
  .main-container {
    margin-left: 0 !important;
  }
}
</style>
