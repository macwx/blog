# blog 前台（Vue 3 + Vite + Vuetify）

基于 **Vue 3 + Vite 5 + Vuetify 3** 的博客前台单页应用。完整项目说明、环境配置与部署教程见仓库根目录 [README](../../README.md)。

## 环境要求

- Node.js **18+**（推荐 20 / 22 LTS）
- npm 9+（国内网络建议使用 npmmirror 源）

## 开发

```bash
npm install --registry=https://registry.npmmirror.com
npm run dev        # 开发模式，访问 http://localhost:8080
```

## 生产构建

```bash
npm run build      # 产物输出到 dist/
npm run preview    # 本地预览构建产物
```

## 接口代理

- 开发环境代理配置在 `vite.config.js` 的 `server.proxy`：
  - `/api` → 后端地址（默认指向作者线上后端 `http://62.234.32.203:8080`，**本地开发请改为自己的后端**）
  - `/clawdchat-api` → `https://clawdchat.cn`（聊天功能，可按需移除）
- 生产环境由 Nginx 将 `/api` 反代到后端（见根 README 部署章节）

## 关键依赖版本

| 依赖 | 版本 |
|------|------|
| vue | ^3.4.0 |
| vuetify | ^3.5.0 |
| vue-router | ^4.3.0 |
| pinia | ^2.1.7 |
| vite | ^5.2.0 |
| axios | ^1.7.0 |
| markdown-it | ^14.0.0 |
| highlight.js | ^11.9.0 |

> 完整依赖列表见 `package.json`；Vue 2 旧版配置见 `package.json.vue2.bak`。
