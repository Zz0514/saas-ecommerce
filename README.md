# SaaS 电商网站（脚手架）

技术栈：**Java 17 + Spring Boot 3**（后端） · **MySQL 8**（数据库） · **Vue3 + Element Plus**（顾客端商城） · **React 18 + Ant Design**（管理后台） · **Docker / Docker Compose**（本地一键运行） · **Git + GitHub + GitHub Actions**（版本管理与 CI）。

> 当前为「骨架版」：已跑通 用户注册/登录(JWT)、商品增删查、分类、前后端分离与容器化。后续可在其上逐步扩展购物车下单、支付、多租户商家入驻等模块。

## 目录结构

```
saas-ecommerce/
├── backend/            # Spring Boot 后端（8080）
│   ├── src/main/java/com/example/ecommerce/   # 代码：config/security/model/repository/service/controller/dto
│   ├── src/main/resources/application.yml
│   ├── pom.xml
│   └── Dockerfile
├── frontend-store/     # Vue3 顾客端商城（开发 5173，容器 80）
├── frontend-admin/     # React 管理后台（开发 5174，容器 80）
├── docker-compose.yml  # 一键起 mysql + backend + store + admin
├── .github/workflows/  # GitHub Actions CI
└── deploy/             # Ubuntu + Docker 部署指南
```

## 快速开始（本地 Docker，推荐）

前置：安装 Docker 与 Docker Compose。

```bash
# 在项目根目录
docker compose up --build
```

启动后访问：
- 顾客端商城：http://localhost:5173
- 管理后台：http://localhost:5174
- 后端 API：http://localhost:8080/api/products

数据库（MySQL）在 localhost:3306，库名 `ecommerce`，用户/密码 `ecommerce/ecommerce123`。

## 本地开发（不依赖 Docker）

### 后端
```bash
cd backend
# 需要本地 MySQL 并把 application.yml 的 mysql 主机改为 localhost
mvn spring-boot:run
```

### 顾客端（Vue）
```bash
cd frontend-store
npm install
npm run dev   # http://localhost:5173，已配置 /api 代理到 8080
```

### 管理后台（React）
```bash
cd frontend-admin
npm install
npm run dev   # http://localhost:5174
```

## 接口示例

```bash
# 注册
curl -X POST http://localhost:8080/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"username":"alice","password":"123456"}'

# 登录拿 token
curl -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"alice","password":"123456"}'

# 带 token 调用受保护接口
curl http://localhost:8080/api/products \
  -H 'Authorization: Bearer <token>'
```

## 接下来可以做的

1. 购物车 → 下单 → 订单状态流转（已有 Order/OrderItem/Cart 实体）。
2. 支付接入（微信支付 / 支付宝沙箱）。
3. 多租户：商家注册入驻、数据按 tenant 隔离（向 SaaS 演进）。
4. 文件上传（商品图片）、搜索、缓存（Redis）。
5. 部署到 Ubuntu 云服务器（见 deploy/ubuntu-deploy.md）。
