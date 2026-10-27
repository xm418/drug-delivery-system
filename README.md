# 县域医共体药品配送管理系统

毕业设计项目。前后端分离：Spring Boot 3.2 + Vue 3 + MySQL 8。

## 一、环境要求

| 软件 | 版本 | 说明 |
|------|------|------|
| JDK | 21 | 本项目用 Java 21 编译运行 |
| Node.js | 18+ | 前端开发服务器 |
| MySQL | 8.0+ | 数据库 |
| Maven | 用项目自带 mvnw | 不用单独装 |

## 二、数据库初始化

1. 启动 MySQL 服务。
2. 导入脚本（会创建 `drug_delivery_v2` 库并写入演示数据）：

```sql
source database/drug_delivery_v2.sql
```

或用 Navicat 等工具直接运行 `database/drug_delivery_v2.sql`。

3. 数据库账号密码在 `backend/src/main/resources/application.yml` 里配置，默认 `root / 339732`，按自己机器改。

## 三、启动后端（端口 8080）

```powershell
cd backend
# Windows PowerShell：
.\mvnw.cmd spring-boot:run
```

看到 `医共体药品配送管理系统启动成功` 即启动完成。接口根路径：`http://localhost:8080/api`

## 四、启动前端（端口 3000）

```powershell
cd frontend
npm install
npm run dev
```

浏览器访问 `http://localhost:3000`，Vite 已配置代理把 `/api` 转发到 8080。

## 五、演示账号（密码都是 123456）

| 账号 | 角色 | 能看到的菜单 |
|------|------|--------------|
| admin | 系统管理员 | 全部（含用户管理） |
| pharmacy01 | 县级管理员 | 机构/分类/药品/库存/订单/配送 |
| township01 | 乡级管理员 | 库存/订单/配送 |
| village01 | 村级用户 | 库存/订单 |
| delivery01 | 配送员 | 配送任务 |

权限只做到**菜单级**（路由守卫 + 侧边栏按角色过滤），没做按钮级，这是设计上的取舍。

## 六、核心业务流程

1. **下单**：村级/乡级用户在「订单管理」创建订单（选药品、填数量）
2. **审核**：县级管理员在「订单管理」审核，通过或驳回
3. **建配送任务**：县级在「配送任务管理」给已审核订单建任务（从哪发到哪）
4. **开始配送**：任务状态 待发货 → 配送中
5. **完成配送**：配送中 → 已完成，订单状态同步变成「已完成」
6. **库存**：支持入库、出库、调拨（一出一入，@Transactional 保证一致性）；库存低于预警阈值自动进「库存预警」列表，首页大屏也能看到

## 七、技术栈与目录

- 后端 `backend/`：Spring Boot 3.2 + MyBatis Plus 3.5 + JWT（jjwt 0.11.5），分层 controller / service / mapper / entity / dto / config
- 前端 `frontend/`：Vue 3 组合式 API + Vite 5 + Element Plus + Pinia + Vue Router + Axios + ECharts
- 数据库 `database/`：10 张表 —— sys_user、sys_role、institution、drug_category、drug、stock、stock_record、drug_order、order_item、delivery_task

## 八、说明

- 密码用 MD5 加密存储（学习用，生产环境应换 BCrypt）
- 删除都是逻辑删除（deleted 字段），不是物理删除
- 配送路径优化（蚁群算法 VRPTW）是后续扩展方向，本版本未实现
