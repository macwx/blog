<template>
  <div class="rightside" :style="isShow">
    <div :class="'rightside-config-hide ' + isOut">
      <i :class="'iconfont rightside-icon ' + icon" @click="check" />
    </div>
    <div class="setting-container" @click="show">
      <i class="iconfont iconshezhi setting" />
    </div>
    <i @click="backTop" class="iconfont rightside-icon iconziyuanldpi" />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import { useTheme } from "vuetify";

const theme = useTheme();

const isShow = ref("");
const isOut = ref("rightside-out");
const icon = ref("iconyueliang");

function backTop() {
  window.scrollTo({
    behavior: "smooth",
    top: 0
  });
}

function scrollToTop() {
  let scrollTop =
    window.pageYOffset ||
    document.documentElement.scrollTop ||
    document.body.scrollTop;
  if (scrollTop > 20) {
    isShow.value = "opacity: 1;transform: translateX(-38px);";
  } else {
    isShow.value = "";
  }
}

function show() {
  const flag = isOut.value === "rightside-out";
  isOut.value = flag ? "rightside-in" : "rightside-out";
}

function check() {
  const flag = icon.value === "iconyueliang";
  icon.value = flag ? "icontaiyang" : "iconyueliang";
  theme.global.name.value =
    theme.global.name.value === "light" ? "dark" : "light";
}

onMounted(() => {
  window.addEventListener("scroll", scrollToTop);
});

onUnmounted(() => {
  window.removeEventListener("scroll", scrollToTop);
});
</script>

<style scoped>
.rightside {
  z-index: 4;
  position: fixed;
  right: -38px;
  bottom: 85px;
  transition: all 0.5s;
}
.rightside-config-hide {
  transform: translate(35px, 0);
}
.rightside-out {
  animation: rightsideOut 0.3s;
}
.rightside-in {
  transform: translate(0, 0) !important;
  animation: rightsideIn 0.3s;
}
.rightside-icon,
.setting-container {
  display: block;
  margin-bottom: 2px;
  width: 30px;
  height: 30px;
  background-color: #49b1f5;
  color: #fff;
  text-align: center;
  font-size: 16px;
  line-height: 30px;
  cursor: pointer;
}
.rightside-icon:hover,
.setting-container:hover {
  background-color: #ff7242;
}
.setting-container i {
  display: block;
  animation: turn-around 2s linear infinite;
}
@keyframes turn-around {
  0% {
    transform: rotate(0);
  }
  100% {
    transform: rotate(360deg);
  }
}
@keyframes rightsideOut {
  0% {
    transform: translate(0, 0);
  }
  100% {
    transform: translate(30px, 0);
  }
}
@keyframes rightsideIn {
  0% {
    transform: translate(30px, 0);
  }
  100% {
    transform: translate(0, 0);
  }
}
</style>
