# 虚拟仿真实训开放平台运行说明

这份说明用于确认项目解压到其他电脑后也能跑出效果。资源封面图片已经放在 `backend/files`，10 条虚拟仿真资源和实训室样例数据在 `backend/sql/vr_training_seed_safe.sql`。

## 环境要求

- JDK 17
- Maven 3.8+
- Node.js 18+
- MySQL 8 或 MySQL 9

## 1. 导入数据库

先创建数据库：

```sql
CREATE DATABASE IF NOT EXISTS db_ich DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

再按顺序导入：

```bash
mysql -u root -p db_ich < db_ich.sql
mysql -u root -p db_ich < backend/sql/vr_training_seed_safe.sql
```

第二个脚本会补齐第2人模块需要的表，并写入：

- 10 条有实验入口链接的虚拟仿真资源
- 10 张资源封面图片路径
- 3 条实训室样例数据
- 共享开放申请和预约所需表结构

## 2. 启动后端

后端默认连接本机 MySQL 的 `db_ich` 数据库，账号密码默认是 `root / 20051105aA`，端口是 `8088`。

```bash
cd backend
mvn spring-boot:run
```

如果别人电脑上的数据库账号密码不同，可以先设置环境变量再启动。

Windows PowerShell 示例：

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="你的数据库密码"
$env:DB_NAME="db_ich"
$env:SERVER_PORT="8088"
mvn spring-boot:run
```

后端接口地址：

```text
http://localhost:8088/api
```

封面图片示例：

```text
http://localhost:8088/api/file/files/vr-cover-01-smart-vehicle-test.png
```

## 3. 启动前端

```bash
cd frontend
npm ci
npm run dev
```

前端默认端口是 `8082`，默认接口地址是 `http://localhost:8088/api`。

访问页面：

```text
http://localhost:8082/front/resources
http://localhost:8082/front/openShare
http://localhost:8082/front/myOpenShare
```

如果后端端口改了，可以在 `frontend/.env.development` 中修改：

```text
VITE_API_BASE_URL='http://localhost:8088/api'
```

## 4. 第2人模块当前完成内容

- 虚拟仿真资源中心：前台资源列表、详情页、后台资源管理、资源封面、资源入口链接。
- 共享开放管理：共享开放首页、资源申请、实训室预约、我的共享开放。
- 资源申请审批通过后会扣减可用资源数。
- 用户归还资源后会恢复可用资源数。
- 资源数量为 0 时不能申请。
- 用户未获得已通过且使用中的资源申请时，不能进入实验。

## 5. 第5人模块当前完成内容（用户与权限管理 + 教学过程监控）

### 用户与权限管理（RBAC）

- 独立角色表（role）、权限表（permission），支持角色-权限多对多关联。
- 用户-角色多对多关联（user_role），不再仅依赖 user 表的 role 字段。
- 对接 Sa-Token 权限体系：实现了 StpInterface 从数据库加载权限码和角色标识，`@SaCheckPermission` 注解生效。
- 后端新增接口：`/role/*`、`/permission/*`（含 `/permission/current` 获取当前用户权限码）、`/userRole/*`、`/rolePermission/*`。
- 前端新增页面：角色管理（`/role`）、权限管理（`/permission`），用户管理页面增加用户类型和院校字段。
- 权限码命名规范为 `表名:操作`（如 `user:add`），前端 `hasPermission()` 自动适配中英文映射。
- 种子数据：admin 角色拥有所有权限，teacher 角色有监控和指导权限，student 角色仅有查看权限。
- 执行 `backend/sql/migration_v5.sql` 可完成 user 表改造 + 6 张新表的创建 + 种子数据写入。

### 教学过程监控

- 教学监控看板（`/teachingMonitor`）：实时统计卡片（在训学生数、平均进度、异常数、完成率）+ 进度列表（含进度条、状态标签）。
- 教师指导记录（`/teachingGuidance`）：支持新增/编辑/删除，按教师ID和学生ID筛选。
- 监控维度覆盖：学生实训进度、学习时长、完成状态、异常信息、教师指导内容。
- 后端新增接口：`/monitorRecord/*`、`/teachingGuidance/*`。

### 数据库迁移

```bash
mysql -u root -p db_ich < backend/sql/migration_v5.sql
```

该脚本新增 `user_type`、`institution` 字段到 user 表，并创建 role、permission、user_role、role_permission、monitor_record、teaching_guidance 六张新表，同时写入种子数据。

### 测试

```bash
python test_module_5.py admin 123456 admin
```

## 6. 打包交付注意事项

压缩包里需要保留这些内容：

- `backend/src`
- `backend/files`
- `backend/sql/vr_training_seed_safe.sql`
- `frontend/src`
- `frontend/package.json`
- `frontend/package-lock.json`
- `db_ich.sql`

不要只复制前端页面，否则数据库里的资源、封面图片和后端接口都不会一起过去。
