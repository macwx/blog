# 超伟博客 - 基于 Spring Boot + Vue 的前后端分离博客系统

<p align="center">
  <img src="https://macwoss.oss-cn-beijing.aliyuncs.com/articles/679251cb5b766dd7a5f9c019d8bdcb10.png" alt="超伟博客" width="600" />
</p>

<p align="center">
  一个功能丰富的开源博客系统，采用 Spring Boot + Vue 前后端分离架构，集成 RBAC 权限管理、WebSocket 实时聊天、第三方 OAuth 登录、多策略搜索与文件上传等企业级特性，代码遵循阿里巴巴开发规范，适合新手入门学习 Java 规范化编程。
</p>

## 在线预览

| 项目 | 地址 |
|------|------|
| 博客前台 | [www.macw.cc](https://www.macw.cc) |
| 后台管理 | [blog.macw.cc](https://blog.macw.cc) |
| 接口文档 | [www.macw.cc/api/doc.html](https://www.macw.cc/api/doc.html) |
| Gitee 仓库 | [gitee.com/macw/blog](https://gitee.com/macw/blog) |

> 测试账号：`test@qq.com` / `1234567`，可登录后台体验。

## 项目特点

- 前台参考 Hexo Butterfly 主题设计，响应式布局，支持深色模式
- 后台参考 Element Admin 设计，侧边栏 + 面包屑 + 历史标签自动生成
- Markdown 编辑器，支持代码高亮、复制、图片预览
- 评论支持表情回复，样式参考 Valine
- 集成在线音乐播放器，支持搜索歌曲
- 支持 QQ、微博第三方登录
- 发布说说、弹幕墙留言、在线聊天室
- RBAC 动态权限管理，前端菜单和后台权限实时更新
- 多种搜索模式（Elasticsearch / MySQL）、多种上传方式（OSS / COS / 本地）可配置
- AOP 注解实现操作日志管理
- 代码遵循阿里巴巴开发规范

## 技术栈

| 层级 | 技术 |
|------|------|
| 前台（blog-vue/blog） | Vue 3、Vite 5、Vuetify 3、Pinia、Vue Router 4、Axios、Markdown-it、KaTeX、Highlight.js |
| 后台（blog-vue/admin） | Vue 2、Vuex、Element UI、Axios、ECharts |
| 后端 | Spring Boot 2.4.1、Spring Security、MyBatis-Plus、WebSocket、Knife4j |
| 数据库 | MySQL 8、Redis、Elasticsearch（可选） |
| 消息队列 | RabbitMQ + MaxWell（可选） |
| 其他 | 阿里云 OSS、腾讯云 COS、QQ / 微博 OAuth |

## 依赖版本

### 前台（`blog-vue/blog/package.json`）

| 依赖 | 版本 |
|------|------|
| vue | ^3.4.0 |
| vuetify | ^3.5.0 |
| vue-router | ^4.3.0 |
| pinia | ^2.1.7 |
| pinia-plugin-persistedstate | ^3.2.1 |
| axios | ^1.7.0 |
| vite | ^5.2.0（要求 Node.js ≥ 18） |
| @vitejs/plugin-vue | ^5.0.0 |
| vite-plugin-vuetify | ^2.0.0 |
| markdown-it | ^14.0.0（+ abbr / container / deflist / emoji / footnote / ins / katex-external / mark / sub / sup / task-lists 插件） |
| highlight.js | ^11.9.0 |
| @mdi/font | ^7.4.47（由 vuetify 本地打包，无 CDN 依赖） |
| dayjs | ^1.11.10 |
| tocbot / nprogress / clipboard / easy-typer-js / recorderx | ^0.2.0 ~ ^4.x |

### 后台（`blog-vue/admin/package.json`）

vue ^2.6.11、element-ui ^2.15.5、@vue/cli ^4.4.0

### 后端（`blog-springboot/pom.xml`）

Spring Boot 2.4.1、Java 1.8、MyBatis-Plus、Spring Security、MySQL Connector、RabbitMQ、Elasticsearch（可选）

## 环境要求

| 环境 | 版本 |
|------|------|
| JDK | 1.8 |
| Maven | 3.6+ |
| MySQL | 8.0+ |
| Redis | 6.0+ |
| Node.js | 18+（推荐 20 LTS / 22 LTS，Vite 5 要求 ≥ 18） |
| Elasticsearch | 7.9+（可选，关闭搜索模式可不安装） |
| RabbitMQ | 3.8+（可选） |

## 环境配置

### 后端（blog-springboot）

- 配置集中在 `src/main/resources/application*.yml`，`application.yml` 仅指定激活环境：`spring.profiles.active: test`
- **`application-test.yml` / `application-prod.yml` 包含数据库密码等敏感信息，已被 `.gitignore` 排除，不会随仓库分发**——部署时需参照模板自行创建，至少包含：
  - MySQL 连接（库名 `blog`，可用根目录 `blog-mysql8.sql` 初始化）
  - Redis 连接
  - 可选：OSS / COS 上传、QQ / 微博 OAuth、邮箱、Elasticsearch
- 服务端口默认 `8080`

### 前台（blog-vue/blog）

- 开发环境接口代理配置在 `vite.config.js` 的 `server.proxy`：
  - `/api` → 后端地址（仓库默认指向作者线上后端 `http://62.234.32.203:8080`，**本地开发请改为自己的后端地址**）
  - `/clawdchat-api` → `https://clawdchat.cn`（聊天功能，可按需移除）
- 生产环境由 Nginx 将 `/api` 反代到后端，见下方部署教程
- 无 `.env` 文件依赖，站点名称、头像等运行期配置由后端 API 下发

## 快速开始

### 1. 初始化数据库

```bash
mysql -u root -p < blog-mysql8.sql
```

### 2. 启动后端

```bash
cd blog-springboot
# 创建 application-test.yml（参照上文环境配置，填写 MySQL / Redis 连接）
mvn spring-boot:run
```

启动后访问 [http://localhost:8080/doc.html](http://localhost:8080/doc.html) 查看接口文档。

### 3. 启动前台

```bash
cd blog-vue/blog
npm install          # 国内网络可加 --registry=https://registry.npmmirror.com
npm run dev          # 开发模式，访问 http://localhost:8080
```

生产构建：`npm run build`，产物输出到 `blog-vue/blog/dist/`。

### 4. 启动后台（可选）

```bash
cd blog-vue/admin
npm install
npm run serve
```

> **注意：** 请先启动后端，再启动前端。前端配置由后端 API 动态加载。
> 默认登录账号：`admin@qq.com` / `1234567`

## 部署

### 生产部署（宝塔面板 / 原生 Nginx 示例）

**后端**

```bash
cd blog-springboot
mvn package -DskipTests              # 产物 target/*.jar
# 上传到服务器后运行（建议用 systemd 托管，配置 Restart=always）：
nohup java -jar -Xms256M -Xmx1024M blog-springboot-agent.jar > /var/log/blog.log 2>&1 &
```

> 服务器内存紧张时建议调小堆内存（如 `-Xmx512M`），避免与 MySQL / Redis 等抢占内存导致卡死。

**前台**

```bash
cd blog-vue/blog
npm install --registry=https://registry.npmmirror.com
npm run build                       # 产出 dist/
# 将 dist/ 内容上传到站点根目录（如 /www/wwwroot/www.macw.cc/），保持目录结构
```

**Nginx 配置要点**（SPA 路由回退 + API 反代 + HTTPS）

```nginx
server {
    listen 443 ssl http2;
    server_name www.macw.cc;
    root /www/wwwroot/www.macw.cc;
    index index.html;

    ssl_certificate     /www/server/panel/vhost/cert/www.macw.cc/fullchain.pem;
    ssl_certificate_key /www/server/panel/vhost/cert/www.macw.cc/privkey.pem;

    # SPA 路由回退（刷新不 404）
    location / { try_files $uri $uri/ /index.html; }

    # 后端 API 反代
    location /api/ {
        proxy_pass http://127.0.0.1:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}

server {
    listen 80;
    server_name www.macw.cc;
    return 301 https://$host$request_uri;   # HTTP 强制跳 HTTPS
}
```

- SSL 证书：acme.sh 签发 Let's Encrypt（`acme.sh --issue -d www.macw.cc --alpn`；若 80 端口被云厂商备案拦截，勿用 HTTP-01 验证）
- 部署后执行 `nginx -t && /etc/init.d/nginx reload`
- 更多教程：[Docker 部署教程](https://www.macw.cc/articles/123) ｜ [环境安装教程](https://www.macw.cc/articles/126) ｜ [项目配置教程](https://www.macw.cc/articles/124)

## 截图

<p align="center">
  <img src="https://macwoss.oss-cn-beijing.aliyuncs.com/photos/20a95856b49fada7fc983e58f07e9d52.png" alt="首页" width="800" />
</p>

## 注意事项

- 前台已由 Vue 2 升级为 **Vue 3 + Vite 5**（旧版配置见 `blog-vue/blog/package.json.vue2.bak`），启动命令为 `npm run dev`，不再是 `npm run serve`
- 运行后可在后台「网站配置」处修改博客名称、头像等信息
- 邮箱配置和第三方登录（QQ / 微博）需要自行申请
- Elasticsearch 需先创建索引，详见项目运行环境教程
- 本地上传模式需要配置 Nginx 映射

## 交流

- QQ：1055215129
- 欢迎提交 Issue 和 PR

如果觉得不错，欢迎 Star 支持！
