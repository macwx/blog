import { ref } from "vue";

const show = ref(false);
const message = ref("");
const type = ref("normal");

function showToast(options, duration = 2000) {
  message.value = options.message;
  type.value = options.type;
  show.value = true;
  setTimeout(() => {
    show.value = false;
  }, duration);
}

export function useToast() {
  return { show, message, type, toast: showToast };
}
