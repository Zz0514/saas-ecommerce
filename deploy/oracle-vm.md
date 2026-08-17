# 部署到 Oracle Cloud 永久免费 Ubuntu 虚拟机

Railway 控制台在你这边不稳定，改用 **Oracle Cloud Free Tier**（永久免费、资源足、正好契合你最初「ubuntu」的需求）。
本仓库自带 `docker-compose.yml`，在 Ubuntu 上一条命令即可把「MySQL + 后端 + 商城 + 后台」全部跑起来。
前端 nginx 已配置 `/api` 同源反代，所以**不需要配 CORS、不需要暴露后端端口**，浏览器直接同源访问。

---

## 0. 前置条件
- 一张信用卡（Oracle 注册要验证，免费额度不会扣费；只有超过免费额度才计费）。
- 能 SSH 的终端（Windows 可用 Git Bash / PowerShell / WSL）。

> 区域说明：Oracle 在中国大陆无节点，最近的是东京 / 首尔 / 新加坡 / 孟买，延迟中等，演示够用。

---

## 1. 注册并创建永久免费虚拟机
1. 打开 https://www.oracle.com/cloud/free/ ，点 **Start for free**，用邮箱注册（需填信用卡验证）。
2. 进入控制台后，左侧菜单 **Compute → Instances → Create instance**。
3. 配置：
   - **Name**：随便，如 `saas-vm`
   - **Image**：选 `Ubuntu` → `Canonical Ubuntu 22.04`
   - **Shape**：点 `Change shape` → `Ampere` → `VM.Standard.A1.Flex`，
     **OCPU = 4，Memory = 24 GB**（这是「Always Free」额度上限，免费的）
   - **Networking**：选默认 VCN，**勾选「Assign a public IPv4 address」**（必须，否则外网访问不了）
   - **Add SSH keys**：选 `Generate a key pair`，下载私钥 `ssh-key-*.key`（保存到本地，后面要用）
4. 点 **Create**。等状态变成 `Running`，记下页面的 **Public IP Address**（后面叫 `<你的公网IP>`）。

---

## 2. 开放防火墙端口（两步都要做）
Oracle 有**两层**防火墙，必须都放行，否则外网打不开。

### 2.1 VCN 安全列表（网络层）
1. 实例详情页 → **Primary VNIC** 里的 **Subnet** 链接 → **Security Lists** → 点默认那条。
2. **Add Ingress Rule**，添加两条（每条加完点一次）：
   - 规则 A：`Source CIDR = 0.0.0.0/0`，`Destination Port = 80`，协议 TCP
   - 规则 B：`Source CIDR = 0.0.0.0/0`，`Destination Port = 8080`，协议 TCP
   - （`22` SSH 默认已开放，不用动；**3306 不要开放**，数据库只在内部用）

### 2.2 实例系统防火墙（ufw，保险起见）
登录后执行（见第 3 步），跑这两句：
```bash
sudo ufw allow 80/tcp
sudo ufw allow 8080/tcp
```

---

## 3. SSH 登录并安装 Docker
把第 1 步下载的私钥放到本地某处（如 `C:\Users\Administrator\.ssh\oracle.key`），
Git Bash 里登录（注意私钥权限）：
```bash
chmod 400 /c/Users/Administrator/.ssh/oracle.key
ssh -i /c/Users/Administrator/.ssh/oracle.key ubuntu@<你的公网IP>
```
（Windows PowerShell 用 `ssh -i C:\Users\Administrator\.ssh\oracle.key ubuntu@<IP>`）

登录后，**复制粘贴下面整段**，一键装好 Docker + Docker Compose + Git：
```bash
sudo apt update -y
sudo apt install -y git ca-certificates curl
# 安装 Docker
curl -fsSL https://get.docker.com | sudo sh
sudo usermod -aG docker $USER
# 让 docker 命令免 sudo 立即生效（重新登录也可）
newgrp docker
docker --version && docker compose version
```

> 注意：`newgrp docker` 后若提示 `docker compose` 找不到，重新 SSH 登录一次即可。

---

## 4. 拉代码 + 启动
```bash
git clone https://github.com/Zz0514/saas-ecommerce.git
cd saas-ecommerce
docker compose up -d --build
```
首次会构建三个镜像（Maven 打后端 jar + 两次 Node 构建），**约 3–8 分钟**，耐心等。
构建完 `docker compose ps` 应看到 4 个容器都是 `Up` 状态。

---

## 5. 验证
- 商城首页：浏览器打开 `http://<你的公网IP>/` （80 端口）
- 管理后台：浏览器打开 `http://<你的公网IP>:8080/`
- 接口自检：`http://<你的公网IP>/api/products` 应返回商品 JSON 数组

后台登录：后端默认没有账号，需要先注册。商城页或后台若提供注册入口即可；
也可进容器手动建管理员（可选，后续扩展）。

---

## 6. 日常运维命令
```bash
cd ~/saas-ecommerce
docker compose ps                 # 看容器状态
docker compose logs -f backend    # 看后端日志（store / admin / mysql 同理）
docker compose restart            # 重启全部
docker compose down               # 停止并删除容器（数据卷保留）
docker compose up -d --build     # 改完代码重新构建部署
```
查看公网 IP：`curl -s ifconfig.me`

---

## 7. 注意事项
- **永久免费**：4 OCPU / 24GB 的 Ampere 实例属于 Always Free，不超时、不扣费；只要不创建额外付费资源。
- **镜像架构**：我们用的 `mysql:8.0` / `node:18-alpine` / `eclipse-temurin:17` / `nginx:alpine` / `maven` 都是多架构镜像，在 ARM（Ampere）上能直接跑，无需改动。
- **数据持久化**：MySQL 数据在 Docker 卷 `mysql-data` 里，`docker compose down` 不会丢；只有 `docker compose down -v` 才清数据。
- **重启 VM 后**：容器不会自动起。可设开机自启（进阶），或登录后重新 `docker compose up -d`。
- **延迟**：机房在境外，国内访问有几十到一百多毫秒延迟，演示 / 面试展示完全够用。
- **域名（可选）**：想用域名访问，在域名解析把 A 记录指向 `<你的公网IP>`，并把 80/8080 的访问换成域名即可（nginx 已支持）。
