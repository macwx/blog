import { createRouter, createWebHistory } from "vue-router";

const routes = [
  {
    path: "/",
    component: () => import("../views/home/Home.vue"),
  },
  {
    path: "/articles/:articleId",
    component: () => import("../views/article/Article.vue"),
  },
  {
    path: "/archives",
    component: () => import("../views/archive/Archive.vue"),
    meta: { title: "归档" },
  },
  {
    path: "/albums",
    component: () => import("../views/album/Album.vue"),
    meta: { title: "相册" },
  },
  {
    path: "/talks",
    component: () => import("../views/talk/Talk.vue"),
    meta: { title: "说说" },
  },
  {
    path: "/talks/:talkId",
    component: () => import("../views/talk/TalkInfo.vue"),
    meta: { title: "说说" },
  },
  {
    path: "/albums/:albumId",
    component: () => import("../views/album/Photo.vue"),
  },
  {
    path: "/tags",
    component: () => import("../views/tag/Tag.vue"),
    meta: { title: "标签" },
  },
  {
    path: "/categories",
    component: () => import("../views/category/Category.vue"),
    meta: { title: "分类" },
  },
  {
    path: "/categories/:categoryId",
    component: () => import("../views/article/ArticleList.vue"),
  },
  {
    path: "/tags/:tagId",
    component: () => import("../views/article/ArticleList.vue"),
  },
  {
    path: "/links",
    component: () => import("../views/link/Link.vue"),
    meta: { title: "友链列表" },
  },
  {
    path: "/about",
    component: () => import("../views/about/About.vue"),
    meta: { title: "关于我" },
  },
  {
    path: "/message",
    component: () => import("../views/message/Message.vue"),
    meta: { title: "留言板" },
  },
  {
    path: "/clawdchat",
    component: () => import("../views/clawdchat/ClawdChat.vue"),
    meta: { title: "社区动态" },
  },
  {
    path: "/user",
    component: () => import("../views/user/User.vue"),
    meta: { title: "个人中心" },
  },
  {
    path: "/oauth/login/qq",
    component: () => import("../components/OauthLogin.vue"),
  },
  {
    path: "/oauth/login/weibo",
    component: () => import("../components/OauthLogin.vue"),
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
