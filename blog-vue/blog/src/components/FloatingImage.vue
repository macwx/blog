<template>
  <div id="floating-image">
    <img src="https://macwoss.oss-cn-beijing.aliyuncs.com/config/83c25cbf0399ab91f569d6fa13af6662.png" alt="Floating Image" id="floating-img" />
    <span id="close-btn" @click="closeImage">❌</span>
  </div>
</template>

<script>
export default {
  name: 'FloatingImage',
  methods: {
    closeImage() {
      this.$el.style.display = 'none';
    }
  }
}
</script>

<style scoped>
/* 悬浮图片的样式 */
#floating-image {
  position: fixed;
  left: 10px;
  top: 50%;
  transform: translateY(-50%);
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: slide-up-down 3s ease-in-out infinite; /* 缓慢上下滑动效果 */
}

/* 图片样式 */
#floating-img {
  width: 250px;
  height: auto;
  border-radius: 5px;
}

/* 关闭按钮样式 */
#close-btn {
  position: absolute;
  top: 0;
  right: 0;
  font-size: 20px;
  background-color: rgba(255, 255, 255, 0.6);
  border-radius: 50%;
  cursor: pointer;
  padding: 5px;
}

/* 定义上下滑动的动画 */
@keyframes slide-up-down {
  0% {
    transform: translateY(-50%) translateY(0);
  }
  50% {
    transform: translateY(-50%) translateY(-10px); /* 向上滑动 */
  }
  100% {
    transform: translateY(-50%) translateY(0); /* 恢复原位 */
  }
}
</style>
