<template>
  <v-snackbar v-model="show" location="top" :timeout="2000" :color="color">
    <i :class="icon" style="margin-right: 10px;" />{{ message }}
  </v-snackbar>
</template>

<script setup>
import { ref, watch } from "vue";
import { useToast } from "./useToast";

const { show, message, type } = useToast();
const color = ref("#49b1f5");
const icon = ref("");

watch(type, (value) => {
  switch (value) {
    case "error":
      color.value = "#F56C6C";
      icon.value = "iconfont iconcuowu";
      break;
    case "success":
      color.value = "#52C41A";
      icon.value = "iconfont iconchenggong";
      break;
    case "warnning":
      color.value = "#F57C00";
      icon.value = "iconfont iconchenggong";
      break;
  }
});
</script>
