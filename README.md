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
| 前端 | Vue 2、Vuex、Vue Router、Axios、Vuetify（前台）、Element UI（后台）、ECharts |
| 后端 | Spring Boot 2.4、Spring Security、MyBatis-Plus、WebSocket |
| 数据库 | MySQL 8、Redis、Elasticsearch（可选） |
| 消息队列 | RabbitMQ + MaxWell |
| 其他 | Knife4j（API 文档）、阿里云 OSS、腾讯云 COS、QQ / 微博 OAuth |

## 项目结构

```
blog/
├── blog-springboot/          # 后端 Spring Boot 项目
│   ├── annotation/           #   自定义注解
│   ├── aspect/               #   AOP 切面
│   ├── config/               #   配置类（Security、Redis、MQ、WebSocket 等）
│   ├── constant/             #   常量定义
│   ├── consumer/             #   MQ 消费者
│   ├── controller/           #   REST 控制器
│   ├── dao/                  #   MyBatis-Plus Mapper
│   ├── dto/                  #   数据传输对象
│   ├── enums/                #   枚举类
│   ├── exception/            #   自定义异常
│   ├── handler/              #   Security 过滤器 & 自定义提示
│   ├── service/              #   业务逻辑（接口 + 实现）
│   ├── strategy/             #   策略模式（搜索、上传、登录、文章导入）
│   ├── util/                 #   工具类
│   └── vo/                   #   视图对象
├── blog-vue/
│   ├── blog/                 # 前台博客（Vue + Vuetify）
│   └── admin/                # 后台管理（Vue + Element UI）
├── blog-mysql8.sql           # 数据库初始化脚本（MySQL 8+）
└── README.md
```

## 快速开始

### 环境要求

| 环境 | 版本 |
|------|------|
| JDK | 1.8 |
| MySQL | 8.0+ |
| Redis | 6.0+ |
| Node.js | 12+ |
| Elasticsearch | 7.9+（可选，关闭搜索模式可不安装） |
| RabbitMQ | 3.8+（可选） |

### 1. 初始化数据库

```bash
mysql -u root -p < blog-mysql8.sql
```

### 2. 启动后端

```bash
cd blog-springboot
# 修改 application-dev.yml 中的数据库、Redis 等连接信息
mvn spring-boot:run
```

启动后访问 [http://localhost:8080/doc.html](http://localhost:8080/doc.html) 查看接口文档。

### 3. 启动前台

```bash
cd blog-vue/blog
npm install
npm run serve
```

访问 [http://localhost:8081](http://localhost:8081)。

### 4. 启动后台（可选）

```bash
cd blog-vue/admin
npm install
npm run serve
```

> **注意：** 请先启动后端，再启动前端。前端配置由后端 API 动态加载。

默认登录账号：`admin@qq.com` / `1234567`

## 部署

- Docker 部署教程：[项目部署教程](https://www.macw.cc/articles/123)
- 环境安装教程：[Docker 安装运行环境](https://www.macw.cc/articles/126)
- 项目配置教程：[项目配置教程](https://www.macw.cc/articles/124)

> 推荐使用宝塔面板部署，更加便捷。

## 截图

<p align="center">
  <img src="https://macwoss.oss-cn-beijing.aliyuncs.com/photos/20a95856b49fada7fc983e58f07e9d52.png" alt="首页" width="800" />
</p>

## 注意事项

- 运行后可在后台「网站配置」处修改博客名称、头像等信息
- 邮箱配置和第三方登录（QQ / 微博）需要自行申请
- Elasticsearch 需先创建索引，详见项目运行环境教程
- 本地上传模式需要配置 Nginx 映射

## 交流

- QQ：1055215129
- 欢迎提交 Issue 和 PR

如果觉得不错，欢迎 Star 支持！
