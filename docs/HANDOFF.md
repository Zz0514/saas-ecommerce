# SaaS 电商项目 · 接力开发手册（HANDOFF）

> **用途**：本文件是给「下一个对话窗口」的接手说明书。读完后应能无缝继续开发，无需回看历史聊天。
> **生成时间**：2026-08-18（周二）17:30 GMT+8
> **项目根**：`D:\zz\saas-ecommerce`
> **基座克隆**：`D:\zz\ref\youlai-mall`（已就位，未改动）
> **配套文档**：`docs/jd-and-base-analysis.md`（JD 拆解 + 技术栈反推 + 一周计划，必读）

---

## 0. 一句话现状（先读这段）

我们为「苏州 Java 后端求职」做了一个 **SaaS 电商作品**，本地开发环境已修好并验证可跑；经多源 JD 调研，**决定以 `youlai-mall` 开源项目为基座、补「多租户」自研**，一周内交付。
**当前卡点**：技术方向已分析清楚（JD 文档已写），但「是否正式从 JPA 转 youlai-mall」这个**决策用户尚未在对话里最终确认**（期间 WebSearch 触发 429 限流、聊天还发生过回滚）。下一个窗口第一件事就是**确认这个决策**，再开干。

---

## 1. 会话脉络（怎么走到这的，5 分钟回顾）

| 阶段 | 做了什么 | 结果 |
|------|---------|------|
| ① 搭骨架 | Java17+Spring Boot3+JPA / Vue3 顾客端 / React 管理端 / MySQL / Docker | 本地可跑的单体骨架，`saas-ecommerce` 仓库 |
| ② 注释+上传 | 全量中文注释；生成 ED25519 SSH 密钥；用 SSH 推到 GitHub（MCP 因权限 403 失败，改走 SSH） | GitHub: `https://github.com/Zz0514/saas-ecommerce` |
| ③ 部署尝试 | 先选 Railway → 用户反馈「构建失败+控制台闪退」→ 查清是平台侧问题；改选 **Oracle 永久免费 Ubuntu VM** | `deploy/oracle-vm.md` 已写，代码改 VM 友好形态 |
| ④ 修本地环境 | Maven 损坏（实为 Git Bash `/d/` 路径坑）→ 修复；MySQL 8.0.42 已在跑；后端 jar 构建+8080 跑通；前端构建完成 | 本地完整开发测试环境就绪 |
| ⑤ JD 调研+基座 | 多源搜 JD（携程/牛客/拉勾/智联/苏州外企德科等 15+ 真实来源）→ 反推技术栈 → 选定 youlai-mall 基座 | `docs/jd-and-base-analysis.md` 已写；`youlai-mall` 已克隆到 `D:\zz\ref\` |
| ⑥ 接力准备（本次） | 核对回滚后环境仍完好；重建后端 jar；整理本手册 | 进行中 |

> 注：聊天曾多次回滚，但**磁盘文件/代码未受影响**。每次接手请先用第 4 节命令复核真实状态，不要轻信记忆。

---

## 2. 已确定的技术决策（用户已认可，不要再议）

1. **部署目标**：Oracle Cloud 永久免费 Ubuntu VM（4 核 24G、Ampere A1、Ubuntu 22.04），不走 Railway/Render。
2. **前端分工**：顾客商城用 **Vue3 + Element Plus**；管理后台曾用 **React + Ant Design**（原型），正式交付倾向 **Vue3 admin（youlai-mall-admin）** 以贴 JD。
3. **本地环境三件套已修好**：Maven 3.9.9、MySQL 8.0.42、Node v22 + npm 10。
4. **代码全部中文注释**、推 GitHub 用 **SSH 密钥**（不是 MCP）。
5. **复用开源轮子**：不重复造轮子，以 youlai-mall 为基座。

---

## 3. 待确认的关键决策（下一个窗口必须先问！）

**是否正式执行「JPA → MyBatis-Plus + 复用 youlai-mall」转型？**

- **选项 A（保守）**：保留现有 JPA 单体骨架继续补功能（购物车/下单/后台账号）。最快出 Demo，但**不贴 JD**（JD 要 MyBatis-Plus + 微服务）。
- **选项 B（推荐，贴合 JD）**：以 `D:\zz\ref\youlai-mall` 为基底重建 `saas-ecommerce`，补多租户。**工作量集中在本周，但作品竞争力最强**。
- **选项 C（先看细节）**：把 JD 分析 + 基座对比完整复述后用户再定。

> 判断依据：`docs/jd-and-base-analysis.md` 第 33 行明确指出「JD 要 MyBatis-Plus ≈85%，且苏州岗点名 MyBatis Plus；JPA 非主流」；第 64 行选定 youlai-mall 为主基座。**强烈建议 B**。

---

## 4. 本地环境复核命令（接手第一步必跑）

```bash
# 1) Maven（注意必须用 mvn.cmd，Git Bash 的 mvn 脚本因 /d/ 路径坑会失败）
mvn.cmd -v            # 期望：Apache Maven 3.9.9 + Java 21

# 2) MySQL 是否在跑
D:/tools/mysql-8.0.42-winx64/bin/mysql -u root -e "SELECT VERSION();"   # 期望 8.0.42

# 3) 后端是否在跑（先前为抢端口 kill 过，通常不在）
curl -s -m4 http://localhost:8080/api/products -w "HTTP %{http_code}\n"

# 4) 现有 JPA 后端 jar 是否还在
ls D:/zz/saas-ecommerce/backend/target/*.jar
```

**环境踩坑速查（每条都曾真实卡过，照做即可避坑）：**

| 坑 | 现象 | 正确做法 |
|----|------|---------|
| **Maven 起不来** | Git Bash 里 `mvn -v` 报 `ClassNotFoundException` / `Launcher` 找不到 | 用 **`mvn.cmd`**（Windows 原生启动器）。已写别名进 `~/.bashrc`，但保险起见直接用 `mvn.cmd`。根因：Git Bash 把 `/d/...` 传给 Windows 版 `java.exe`，Java 读不懂 |
| **后端起在随机端口** | 启动后 Tomcat 在 61900/62413 等随机端口 | 本沙箱会注入 `PORT=0`（随机端口）。**用 `--server.port=8080` 命令行参数压过它**（Spring Boot 最高优先级） |
| **写日志 Permission denied** | 重启后端报日志文件「拒绝访问」 | 旧 java 进程被 kill 后日志文件句柄未释放。**每次用新日志文件名**（如 `backend-$(date +%H%M%S).log`），别复用 |
| **`mvn clean`/写 target 拒绝访问** | 二次构建报 target 文件「拒绝访问」 | 上次构建残留文件被锁（Defender 扫描/进程未释放）。**`rm -rf target` 后重建** |
| **npm install 权限错** | `npm run build` 报 vite 不是命令 / 权限错误 | 多因瞬时缓存锁/杀软。**重试 npm install 通常能好**；前端 dist 最终能生成 |
| **路径风格** | Windows 程序不认 `/d/...`、Git Bash 不认 `D:\...` | 给 Windows 程序（java/mysql/mvn.cmd）用 **`D:/...` 正斜杠**；Git Bash 内部操作用 `/d/...` |

**MySQL 库/用户（已建好，勿重复建）：**
- 库：`ecommerce`（utf8mb4）；用户：`ecommerce` / `ecommerce123`（localhost 与 % 都有）
- 启动后端连本地库用：`MYSQL_HOST=localhost MYSQL_PORT=3306 MYSQL_PASSWORD=ecommerce123`

---

## 5. 当前 `saas-ecommerce`（JPA 原型）代码状态

> 路径：`D:\zz\saas-ecommerce`。**这是原型**，若执行选项 B 会被 youlai-mall 改造版覆盖/归档。

**技术栈**：Spring Boot 3.2.5 + Java 21（开发机 JDK21，JPA 版本按 17 写）+ JPA/Hibernate + Spring Security + JWT(jjwt 0.12.6, HS256) + BCrypt；Vue3(顾客端) + React(Ant Design, 管理端)；MySQL 8。

**已实现**：
- 实体：`User, Role, Category, Product, Order, OrderStatus, OrderItem, Cart, CartItem`
- 仓库层（JPA Repository）、DTO（`AuthRequest/AuthResponse/ProductDto`）
- 安全：`JwtUtil`、`JwtAuthenticationFilter`、`CustomUserDetailsService`、`SecurityConfig`、全局异常处理
- 控制器：`AuthController, ProductController, CategoryController`、服务 `AuthService, ProductService`
- `application.yml` 已**环境变量驱动**（PORT/MySQL/JWT/CORS），`SecurityConfig` 从 env 读 CORS 白名单
- 前端 axios `baseURL` 支持 `VITE_API_BASE_URL` / `REACT_APP_API_BASE_URL`；nginx 用 `${PORT}` 模板
- `docker-compose.yml` 已改 VM 友好（商城 80 / 后台 8080 / MySQL 不映射公网）
- 文档：`deploy/oracle-vm.md`、`deploy/railway.md`（弃用）、`docs/jd-and-base-analysis.md`

**已知 bug（已修）**：`SecurityConfig` 曾 import 错误的 `com.example.ecommerce.security.JwtAuthenticationFilter`（实际在 `config` 包），已在 commit `6b7fead` 修复。

**未实现（原型缺口）**：购物车→下单流程、支付、多租户、管理员默认账号初始化、图片上传、Redis 缓存。

**Git 状态提醒**：`docs/`、`frontend-*/package-lock.json` 当前是 **untracked**，尚未提交（见第 7 节「待办」）。

---

## 6. youlai-mall 基座实况（选项 B 的原材料）

> 路径：`D:\zz\ref\youlai-mall`。**已克隆，未被改动**，可直接作为基底。

- **版本**：Spring Boot 2.7.8 + Spring Cloud 2021 / Alibaba 2021；**MyBatis-Plus 3.4.3**（父 pom 管理，业务模块通过继承获得）
- **模块**：`youlai-gateway, youlai-auth, youlai-system, youlai-common, mall-pms(商品), mall-oms(订单), mall-ums(会员), mall-sms(营销), middleware`
- **认证**：`youlai-auth` 含 **OAuth2 + JWT**（`AuthorizationServerConfig`、`TokenEnhanceConfig`）—— 直接贴 JD 的「认证授权」
- **SQL 脚本齐全**：`docs/sql/mysql8.x/` 下 `database.sql, mall_pms.sql, mall_oms.sql, mall_sms.sql, mall_ums.sql, oauth2.sql, youlai_system.sql`
- **多租户**：基座**无**现成 tenant 代码，但 MyBatis-Plus 自带 `TenantLineInnerInterceptor`，加租户层工作量小（参考 yudao-cloud 设计思路，不引其代码）
- **前端**：**不在本克隆内**。`youlai-mall-admin`（Vue3 管理后台）、`youlai-mall-weapp`（uni-app 顾客端/H5）是独立仓库，需另 clone
- **协议**：Apache-2.0（可商用）

**派生仓库建议**（选项 B 执行时）：
```
git clone https://github.com/haoxianrui/youlai-mall-admin   D:/zz/saas-ecommerce/frontend-admin
git clone https://github.com/haoxianrui/youlai-mall-weapp   D:/zz/saas-ecommerce/frontend-store
```
（仓库地址以 GitHub 实际为准，clone 前先 `gh repo search youlai-mall` 或网页确认）

---

## 7. 下一个窗口的「接手第一周」清单

**第一步（必做）**：向用户确认第 3 节的决策（A/B/C）。若选 B，按下面走。

**若执行选项 B（推荐路径）：**
1. **Day1 环境对齐**：本地装 Redis（Windows 版）、Nacos(standalone)；clone youlai-mall-admin / weapp 到 frontend-admin / frontend-store；导入 `docs/sql/mysql8.x/*` 到本地 MySQL（新建 `youlai` 库或复用）。
2. **Day2 跑通基座**：启动 Nacos→Gateway→Auth→System→PMS→OMS→UMS→SMS；admin `npm install && npm run dev`；验证登录/商品/下单。
3. **Day3 多租户（核心）**：在 `youlai-common` 注入 `TenantLineInnerInterceptor` + `tenant_id`；核心表加列；租户上下文拦截器；验证数据隔离。
4. **Day4 顾客端**：uni-app/H5 或现有 Vue store 对接商品/购物车/订单接口，打通浏览→加购→下单。
5. **Day5 后台增强**：Vue3 admin 加「租户管理」菜单 + RBAC 随租户隔离。
6. **Day6 部署**：Oracle VM + 基座 docker-compose（MySQL/Redis/Nacos/RabbitMQ/Seata）一键起；前端 build + nginx。
7. **Day7 收尾**：写 `README`（技术栈↔JD 对照、架构图、多租户说明）；造演示数据；整理面试话术。

**本窗口遗留待办（无论选 A/B 都应先完成）：**
- [ ] 提交 `docs/` 与 `frontend-*/package-lock.json` 到 git（防回滚丢失）。本次已重建后端 jar（见任务 `mHdQ6r`）。
- [ ] 若选 A：给 JPA 后端加「管理员默认账号初始化」（DataInitializer 种子数据）。
- [ ] 若选 B：先 `git checkout -b legacy-jpa` 把现有 JPA 代码归档，再在 main 上以 youlai-mall 重建。

---

## 8. 关键资源索引

| 类型 | 位置 |
|------|------|
| 项目仓库 | `D:\zz\saas-ecommerce`（GitHub: Zz0514/saas-ecommerce） |
| youlai-mall 克隆 | `D:\zz\ref\youlai-mall` |
| JD 分析 + 一周计划 | `D:\zz\saas-ecommerce\docs\jd-and-base-analysis.md` |
| Oracle 部署手册 | `D:\zz\saas-ecommerce\deploy\oracle-vm.md` |
| 工作日志 | `D:\zz\.workbuddy\memory\2026-08-17.md`、`2026-08-18.md` |
| Maven 安装 | `D:\tools\apache-maven-3.9.9`（用 `mvn.cmd`） |
| JDK | `D:\tools\jdk-21.0.12+8`（`JAVA_HOME` 已设） |
| MySQL | `D:\tools\mysql-8.0.42-winx64`（服务名 `MySQL842`，root 无密码，库 `ecommerce`） |
| Node/npm | v22.22.2 / 10.9.7 |

---

## 9. 给下一个窗口的提醒

- 用户是 **27 岁、苏州求职（Java 后端方向）、双非本科、空窗约 1.5 年、时间紧（约 3 个月准备作品）**。给方案要**可执行、分优先级、直接出产物**，别空谈。
- 用户重视「不重复造轮子」「贴 JD」「一周交付」。技术选型一律以「JD 要什么」为准。
- 聊天可能再次回滚——**凡改动先 `git commit`/落盘，凡结论先写文档**，降低回滚损失。
- WebSearch 曾 429 限流，批量搜 JD 时**控制频率、分批进行**，别一次性铺太多。
- 决策未确认前，不要大规模改写代码；先问清方向（见第 3 节）。
