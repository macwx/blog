<template>
  <div v-show="chooseEmoji" class="emoji-wrapper">
    <span
      class="emoji-item"
      v-for="(value, key, index) of emojiList"
      :key="index"
      @click="addEmoji(key)"
    >
      <v-img
        :lazy-src="value"
        :src="value"
        :title="key"
        class="emoji"
        width="24"
        height="24"
      />
    </span>
  </div>
</template>

<script setup>
import EmojiList from "../assets/js/emoji";

defineProps({
  chooseEmoji: {
    type: Boolean
  }
});

const emit = defineEmits(["addEmoji"]);

const emojiList = EmojiList;

function addEmoji(key) {
  emit("addEmoji", key);
}
</script>

<style scoped>
.emoji {
  user-select: none;
  margin: 0.25rem;
  display: inline-block;
  vertical-align: middle;
}
.emoji-item {
  cursor: pointer;
  display: inline-block;
}
.emoji-item:hover {
  transition: all 0.2s;
  border-radius: 0.25rem;
  background: #dddddd;
}
.emoji-wrapper {
  max-height: 150px;
  overflow-y: auto;
}
</style>
