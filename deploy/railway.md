# 部署到 Railway（免费层，公网可访问）

Railway 能直接连 GitHub、原生支持 MySQL 插件、自动给每个服务分配 `*.railway.app` 公网 HTTPS 地址。本仓库已改成「环境变量驱动」，按下面步骤即可上线。

## 1. 注册并新建项目
1. 打开 https://railway.app ，用 GitHub 登录。
2. 新建 Project → 选 **Deploy from GitHub repo** → 选 `saas-ecommerce`。
3. 进入项目后，先添加数据库：点 **New → Database → MySQL**，等它变成 `Active`。

## 2. 添加三个服务（都来自同一个仓库，指定不同目录）
在 Project 里依次 **New → Service → GitHub repo → saas-ecommerce**，每次都展开 **Settings → Source** 把 Root Directory 指到对应目录：

| 服务 | Root Directory | 说明 |
|------|----------------|------|
| backend | `backend` | Spring Boot，Railway 注入 `PORT` 和 MySQL 变量 |
| store   | `frontend-store` | Vue 商城（nginx 静态托管） |
| admin   | `frontend-admin` | React 后台（nginx 静态托管） |

> Railway 会自动用各目录下的 `Dockerfile` 构建。

## 3. 给 backend 配置环境变量
在 backend 服务的 **Variables** 里添加：

```
JWT_SECRET=随便一段至少32位的随机字符串
CORS_ORIGINS=https://<store地址>.railway.app,https://<admin地址>.railway.app
```

- `JWT_SECRET`：必填，否则 Token 无法签发。
- `CORS_ORIGINS`：填下面两个前端服务的公网地址（部署完就能看到），用逗号分隔。
- MySQL 连接信息（`MYSQL_HOST/PORT/USER/PASSWORD/DATABASE`）由 MySQL 插件自动注入，backend 的 `application.yml` 已读取，无需手动填。

## 4. 给两个前端配置 API 地址
前端的 axios `baseURL` 默认是 `/api`（本地用），部署时要指向 backend 公网地址：

- store 服务 Variables：`VITE_API_BASE_URL=https://<backend地址>.railway.app/api`
- admin 服务 Variables：`REACT_APP_API_BASE_URL=https://<backend地址>.railway.app/api`

> 改完变量后，Railway 会重新构建部署。

## 5. 验证
- backend 地址：`https://<backend>.railway.app/api/products` 应返回商品 JSON。
- store 地址：打开应看到商城首页。
- admin 地址：打开应看到登录页，用 backend 注册的账号可登录。

## 6. 注意
- **免费试用额度**：Railway 新账号有约 $5 试用额度，跑这三个小服务足够体验一段时间；额度用完后需升级 hobby（$5/月）。
- 免费层服务一段时间无访问可能会缩容/变慢，属正常。
- 数据库数据在 MySQL 插件里持久化，删除服务才会丢。
- 若需自定义域名，在服务的 Settings → Domains 里绑定。
