# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Full-stack blog application with Spring Boot backend and Vue 2 frontend (separated into public blog and admin panel). Based on the "风丶宇" blog project.

## Repository Structure

- `blog-springboot/` — Spring Boot 2.4.1 backend (Java 8)
- `blog-vue/blog/` — Public-facing blog frontend (Vue 2 + Vuetify)
- `blog-vue/admin/` — Admin management panel frontend (Vue 2 + Element UI)
- `blog-mysql8.sql` — Database schema (MySQL 8+)

## Build & Run Commands

### Backend (Spring Boot)
```bash
cd blog-springboot
mvn clean package              # Build JAR
mvn spring-boot:run            # Run locally (port 8080)
mvn test                       # Run tests
```

Active profile is set in `application.yml` (`spring.profiles.active`). Config files: `application-dev.yml`, `application-test.yml`, `application-prod.yml`.

### Frontend — Public Blog
```bash
cd blog-vue/blog
npm install
npm run serve    # Dev server (proxies /api → localhost:8080)
npm run build    # Production build
npm run lint     # ESLint
```

### Frontend — Admin Panel
```bash
cd blog-vue/admin
npm install
npm run serve    # Dev server (proxies /api → localhost:8080)
npm run build    # Production build
npm run lint     # ESLint
```

**Important:** Start the backend first — the frontend dynamically loads configuration from the backend API.

## Backend Architecture

Package: `com.minzheng.blog`

| Layer | Description |
|-------|-------------|
| `controller` | REST endpoints. Public APIs return `Result` wrapper. |
| `service` / `service.impl` | Business logic. Interface + Impl pattern. |
| `dao` | MyBatis-Plus mappers. XML mappings in `resources/mapper/`. |
| `config` | Spring configurations (Security, Redis, RabbitMQ, WebSocket, MVC, MyBatis-Plus, Knife4j/Swagger). |
| `strategy` / `strategy.impl` | **Strategy pattern** for pluggable behaviors — see below. |
| `enums` | Application enumerations. |
| `dto` / `vo` | Data transfer objects and view objects. |
| `annotation` / `aspect` | Custom annotations (`@OptLog`, `@AccessLimit`) with AOP aspects. |
| `consumer` | RabbitMQ consumers (email, MaxWell CDC). |
| `handler` | Spring Security filter extensions and custom auth responses. |
| `constant` | Redis prefixes, MQ prefixes, operation types, etc. |

### Strategy Pattern (key architectural pattern)

Four strategy contexts control runtime-switchable behavior via Spring profiles/config:

- **SearchStrategyContext** — `mysql` or `elasticsearch` search mode (`search.mode` in config)
- **UploadStrategyContext** — `oss`, `cos`, or `local` file upload (`upload.mode` in config)
- **SocialLoginStrategyContext** — QQ, Weibo third-party login
- **ArticleImportStrategyContext** — Hexo or normal article import

Each has a strategy interface, abstract base impl, and concrete impls selected by context.

### Key Integrations

- **Spring Security** — RBAC model with dynamic permission (`Resource`, `Role`, `Menu` entities)
- **Redis** — Caching, rate limiting (`@AccessLimit`), session data
- **RabbitMQ** — Async email notifications, MaxWell CDC
- **Elasticsearch** — Optional article search (switchable to MySQL via `search.mode`)
- **WebSocket** — Online chat with real-time messaging
- **Knife4j** — API docs at `/doc.html`
- **Third-party login** — QQ, Weibo OAuth
- **File upload** — Alibaba Cloud OSS, Tencent Cloud COS, or local storage

### Configuration Notes

- `search.mode` in application yml controls search backend (`elasticsearch` or `mysql`)
- `upload.mode` controls file storage (`oss`, `cos`, or `local`)
- Default test account: `admin@qq.com` / `1234567` (admin role)
- Test account: `test@qq.com` / `1234567`

## Frontend Architecture

Both Vue apps use Vue 2 with vue-router (history mode) and vuex with `vuex-persistedstate`.

- **Public blog** (`blog-vue/blog`): Vuetify-based UI. Routes: home, articles, archives, albums, talks, tags, categories, links, about, message board, user profile, OAuth callbacks.
- **Admin panel** (`blog-vue/admin`): Element UI-based. Dynamic route loading from backend permissions. Layout includes sidebar, breadcrumb, and tag navigation.

Both proxy `/api` requests to `localhost:8080` in dev mode via `vue.config.js`.

The public blog uses `markdown-it` with many plugins for rendering, `highlight.js` for code blocks, `tocbot` for table of contents, and `nprogress` for loading bars.

## Database

MySQL 8+ required. Schema is in `blog-mysql8.sql` at the repository root. Key entity relationships follow RBAC: User → UserRole → Role → RoleMenu → Menu, and Role → RoleResource → Resource.
