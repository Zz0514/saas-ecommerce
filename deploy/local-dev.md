# 本地开发与环境说明（Windows）

本仓库已在 Windows 本机验证可完整构建与运行。本文记录环境现状、踩过的坑、以及本地启动步骤。

## 1. 环境现状（已实测可用）
| 组件 | 版本 / 路径 | 说明 |
|------|-----------|------|
| JDK | 21 (Temurin, `D:\tools\jdk-21.0.12+8`) | `JAVA_HOME` 已设 |
| Maven | 3.9.9 (`D:\tools\apache-maven-3.9.9`) | **用 `mvn.cmd`**（见坑 1） |
| Node | v22.22.2 / npm 10.9.7 | 前端构建/开发用 |
| MySQL | 8.0.42 (`D:\tools\mysql-8.0.42-winx64`) | 已安装为服务 `MySQL842`，库 `ecommerce` + 用户 `ecommerce/ecommerce123` 已建 |
| Git | 2.55.0 | 推送代码用 |

> 端口约定：后端 `8080`、商城前端 dev `5173`、管理后台 dev `5174`。

## 2. 踩过的坑（重要，避免重蹈）
1. **Maven 在 Git Bash 里直接用 `mvn` 会失败**
   - 现象：`mvn -v` 报 `找不到或无法加载主类 org.codehaus.plexus.classworlds.launcher.Launcher`。
   - 原因：Git Bash 的 shell 启动器把 `/d/...` 路径传给 Windows 版 `java.exe`，Java 读不到文件。
   - 解决：**用 `mvn.cmd`**（Windows 原生启动器）。已在 `~/.bashrc` 加了别名 `alias mvn='mvn.cmd'`，所以 Git Bash 里敲 `mvn` 也会走 `mvn.cmd`。在 PowerShell / cmd 里 `mvn` 本就指向 `mvn.cmd`，无需处理。
2. **npm 默认缓存目录被锁（EPERM）**
   - 现象：`npm install` 长时间卡住后报 `EPERM: operation not permitted, open '...\npm-cache\_cacache\...'`。
   - 原因：缓存目录里的文件被杀软/残留进程占用。
   - 解决：已将 npm 缓存改到未被占用的 `D:\zz\.npm-cache`（`npm config set cache "D:/zz/.npm-cache"`，已全局生效）。之后安装正常（~1 分钟）。
3. **后端随机端口**
   - 后端 `application.yml` 用 `server.port=${PORT:8080}`。正常机器 `PORT` 未设置 → 默认 8080。
   - 若运行环境注入了 `PORT=0`（会被当作随机端口），用命令行参数强制：
     `java -jar target/xxx.jar --server.port=8080`。

## 3. 本地启动步骤
### 3.1 启动 MySQL
```bash
# 以管理员运行（服务已安装为 MySQL842）
net start MySQL842
# 若提示“服务已标记删除/已被占用”，说明实例已在跑，直接连即可
```
库与用户已在初始化时建好，无需再建。

### 3.2 后端
```bash
cd backend
# 构建（跳过测试）
mvn.cmd package -DskipTests
# 运行（本地 MySQL 主机是 localhost，不是 docker 的 mysql）
MYSQL_HOST=localhost MYSQL_PORT=3306 java -jar target/ecommerce-backend-0.0.1-SNAPSHOT.jar --server.port=8080
```
验证：`curl http://localhost:8080/api/products` → 返回 `[]`（HTTP 200）。

> 也可用 `mvn.cmd spring-boot:run`（Maven 会自行管理类路径与端口）。

### 3.3 前端（开发模式，带热更新）
开两个终端：
```bash
# 商城（Vue）
cd frontend-store
npm install          # 缓存已指向 D:\zz\.npm-cache，无需额外参数
npm run dev          # http://localhost:5173
```
```bash
# 管理后台（React）
cd frontend-admin
npm install
npm run dev          # http://localhost:5174
```
`vite.config.js` 已配置 `/api` 代理到 `http://localhost:8080`，因此前端 dev 模式调接口自动转发到本地后端，无需处理跨域。

### 3.4 生产式静态托管（可选）
```bash
cd frontend-store && npm run build   # 生成 dist/
cd frontend-admin && npm run build   # 生成 dist/
```
用 nginx 等静态服务器托管 `dist/`，并把 `/api` 反代到后端 `8080` 即可（与 `deploy/oracle-vm.md` 中容器内的 nginx 反代思路一致）。

## 4. 常见排错
- **后端连不上库**：确认 MySQL 已启动；确认 `MYSQL_HOST=localhost`（本地不是 `mysql` 这个 docker 服务名）。
- **前端调接口 404/跨域**：确认后端在 8080 运行；确认用的是 `npm run dev`（依赖 vite 代理），而非直接打开 `dist/index.html` 静态文件。
- **Maven 报类找不到**：确认用的是 `mvn.cmd` 而不是 Git Bash 的 `mvn` shell 启动器。
- **npm install 又卡住/EPERM**：`npm cache clean --force` 后重试；若仍失败，换缓存目录 `npm install --cache D:/zz/.npm-cache`。
