import { defineStore } from "pinia";
import { ref, computed } from "vue";

export const useBlogStore = defineStore(
  "blog",
  () => {
    // State
    const searchFlag = ref(false);
    const loginFlag = ref(false);
    const registerFlag = ref(false);
    const forgetFlag = ref(false);
    const emailFlag = ref(false);
    const drawer = ref(false);
    const loginUrl = ref("");
    const userId = ref(null);
    const avatar = ref(null);
    const nickname = ref(null);
    const intro = ref(null);
    const webSite = ref(null);
    const loginType = ref(null);
    const email = ref(null);
    const articleLikeSet = ref([]);
    const commentLikeSet = ref([]);
    const talkLikeSet = ref([]);
    const blogInfo = ref({
      websiteConfig: {},
      pageList: [],
      articleCount: 0,
      categoryCount: 0,
      tagCount: 0,
      viewsCount: 0,
    });

    // Getters
    const isLoggedIn = computed(() => !!userId.value);

    // Actions
    function login(user) {
      userId.value = user.userInfoId;
      avatar.value = user.avatar;
      nickname.value = user.nickname;
      intro.value = user.intro;
      webSite.value = user.webSite;
      articleLikeSet.value = user.articleLikeSet || [];
      commentLikeSet.value = user.commentLikeSet || [];
      talkLikeSet.value = user.talkLikeSet || [];
      email.value = user.email;
      loginType.value = user.loginType;
    }

    function logout() {
      userId.value = null;
      avatar.value = null;
      nickname.value = null;
      intro.value = null;
      webSite.value = null;
      articleLikeSet.value = [];
      commentLikeSet.value = [];
      talkLikeSet.value = [];
      email.value = null;
      loginType.value = null;
    }

    function saveLoginUrl(url) {
      loginUrl.value = url;
    }

    function saveEmail(val) {
      email.value = val;
    }

    function updateUserInfo(user) {
      nickname.value = user.nickname;
      intro.value = user.intro;
      webSite.value = user.webSite;
    }

    function updateAvatar(val) {
      avatar.value = val;
    }

    function checkBlogInfo(info) {
      blogInfo.value = info;
    }

    function closeModel() {
      registerFlag.value = false;
      loginFlag.value = false;
      searchFlag.value = false;
      emailFlag.value = false;
    }

    function articleLike(articleId) {
      const idx = articleLikeSet.value.indexOf(articleId);
      if (idx !== -1) {
        articleLikeSet.value.splice(idx, 1);
      } else {
        articleLikeSet.value.push(articleId);
      }
    }

    function commentLike(commentId) {
      const idx = commentLikeSet.value.indexOf(commentId);
      if (idx !== -1) {
        commentLikeSet.value.splice(idx, 1);
      } else {
        commentLikeSet.value.push(commentId);
      }
    }

    function talkLike(talkId) {
      const idx = talkLikeSet.value.indexOf(talkId);
      if (idx !== -1) {
        talkLikeSet.value.splice(idx, 1);
      } else {
        talkLikeSet.value.push(talkId);
      }
    }

    return {
      searchFlag,
      loginFlag,
      registerFlag,
      forgetFlag,
      emailFlag,
      drawer,
      loginUrl,
      userId,
      avatar,
      nickname,
      intro,
      webSite,
      loginType,
      email,
      articleLikeSet,
      commentLikeSet,
      talkLikeSet,
      blogInfo,
      isLoggedIn,
      login,
      logout,
      saveLoginUrl,
      saveEmail,
      updateUserInfo,
      updateAvatar,
      checkBlogInfo,
      closeModel,
      articleLike,
      commentLike,
      talkLike,
    };
  },
  {
    persist: {
      storage: sessionStorage,
    },
  }
);
