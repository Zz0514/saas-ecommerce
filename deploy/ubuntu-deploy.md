# Ubuntu + Docker 部署指南

适用于任何 Ubuntu 20.04/22.04 云服务器（阿里云 / 腾讯云 / 雨云 / 优刻得 等轻量应用服务器均可）。

## 1. 服务器初始化

```bash
# 升级并安装基础工具
sudo apt update && sudo apt upgrade -y
sudo apt install -y git curl

# 安装 Docker
curl -fsSL https://get.docker.com | sudo sh
sudo usermod -aG docker $USER   # 退出重登录后生效

# 安装 Docker Compose 插件（新版已内置，验证一下）
docker compose version
```

## 2. 拉取代码

```bash
git clone https://github.com/<你的用户名>/saas-ecommerce.git
cd saas-ecommerce
```

> 若服务器无法直连 GitHub，可在本地打包 `tar.gz` 上传，或配置 SSH 部署密钥。

## 3. 配置生产环境变量

生产环境**不要**用仓库里的默认密码和 JWT 密钥。推荐用 `.env` 文件（已被 .gitignore 忽略）：

新建 `saas-ecommerce/.env`：

```
MYSQL_ROOT_PASSWORD=换成强密码
MYSQL_PASSWORD=换成强密码
JWT_SECRET=换成至少32位随机串
```

> 当前 `docker-compose.yml` 里 MySQL 密码为硬编码，生产前请改为读取 `.env`（用 `${MYSQL_PASSWORD}` 语法）。这一步可按需改造。

## 4. 一键构建并启动

```bash
docker compose up -d --build
```

查看状态：
```bash
docker compose ps
docker compose logs -f backend
```

## 5. 对外开放（nginx 反代 + 域名，可选但推荐）

在服务器上再起一个 nginx 容器或系统 nginx，把 80/443 反代到三个服务：

- `your-domain.com` → store 容器(5173:80)
- `admin.your-domain.com` → admin 容器(5174:80)
- `api.your-domain.com` → backend 容器(8080)

示例（系统 nginx `/etc/nginx/sites-enabled/ecommerce`）：

```nginx
server {
    listen 80;
    server_name api.your-domain.com;
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
    }
}
```

配置 HTTPS（免费证书）用 `certbot --nginx`，按提示操作即可。

## 6. 后续运维

- 更新代码后：`git pull && docker compose up -d --build`
- 备份数据库：`docker exec ecommerce-mysql mysqldump -u ecommerce -p ecommerce > backup.sql`
- 监控：`docker compose ps` / `docker stats`

## 常见问题

- **后端起不来**：先看 `docker compose logs backend`，多为 MySQL 还没就绪或密码不一致。已配置 `depends_on: condition: service_healthy` 一般可避免。
- **前端白屏/接口 404**：确认 nginx 反代里 `/api` 已指向 backend，且前端打包后 baseURL 正确地走了 `/api`。
- **JWT 报错**：确认 `JWT_SECRET` 长度 ≥ 32 字符。
