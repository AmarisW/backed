# Flyway 使用说明

> 基于本模块（`flywayModel`）整理：数据库版本迁移怎么配、脚本怎么写、怎么验证。  
> 启动类：`com.gqtian.flyway.FlywayModelApplication`  
> 脚本目录：`src/main/resources/db/migration`

---

## 1. Flyway 是什么

Flyway 是数据库 **Schema 版本迁移** 工具。核心思路：

> 把建表、改字段、灌数据等 SQL 当成「带版本号的脚本」管起来，启动时按版本顺序执行未跑过的脚本，并记入 `flyway_schema_history`。

典型用途：多环境 schema 对齐、变更可追溯、避免手工执行 SQL 漏跑/乱序。

---

## 2. 本模块做什么

| 项 | 说明 |
|----|------|
| 启动端口 | `2003` |
| Context Path | `/flywayModel` |
| 目标库 | `flyway_dev`（独立库，不碰 `mybatis_dev`） |
| 自动建库 | JDBC URL 带 `createDatabaseIfNotExist=true` |
| 启动行为 | 自动扫描并执行 `classpath:db/migration` 下脚本 |
| 查询方式 | MyBatis-Plus（Entity / Mapper / Service） |

依赖要点：

- `flyway-core` + `flyway-mysql`（MySQL 8+ 必须带 `flyway-mysql`，否则会报 Unsupported Database）
- `mybatis-plus-spring-boot3-starter` 等，用于验证迁移结果

---

## 3. 快速开始

### 3.1 改数据源

编辑 `src/main/resources/application-base.yml`：

```yaml
spring:
  datasource:
    dynamic:
      primary: master
      datasource:
        master:
          url: jdbc:mysql://<host>:3306/flyway_dev?...&createDatabaseIfNotExist=true
          username: <user>
          password: REDACTED
```

### 3.2 启动

运行 `FlywayModelApplication`。日志里应能看到 Flyway 执行迁移（`org.flywaydb`）。

### 3.3 验证接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/flywayModel/flyway/status` | 总览：历史条数 + 用户数 + 明细 |
| GET | `/flywayModel/flyway/history` | `flyway_schema_history` 列表 |
| GET | `/flywayModel/flyway/users` | `flyway_demo_user` 列表（验证 V1~V3） |

示例：

```text
http://localhost:2003/flywayModel/flyway/status
```

---

## 4. 迁移脚本约定

### 4.1 目录与命名

路径：`src/main/resources/db/migration`

| 类型 | 命名格式 | 示例 | 说明 |
|------|----------|------|------|
| 版本迁移 | `V{版本}__{描述}.sql` | `V1__create_flyway_demo_user.sql` | 只执行一次，版本递增 |
| 可重复脚本 | `R__{描述}.sql` | `R__refresh_view.sql` | checksum 变化时会再执行 |

注意：

- 版本号与描述之间是 **两个下划线** `__`
- **已执行过的脚本不要改内容**（`validate-on-migrate=true` 时会校验失败）
- 需要改表结构时，新增更高版本脚本（如 `V4__xxx.sql`），不要改旧文件

### 4.2 本模块已有脚本

| 脚本 | 作用 |
|------|------|
| `V1__create_flyway_demo_user.sql` | 创建演示用户表 |
| `V2__add_user_email.sql` | 增加 `email` 字段 |
| `V3__insert_demo_data.sql` | 插入 2 条演示数据 |

### 4.3 新增脚本示例

例如要加 `phone` 字段，新增：

```sql
-- 文件名：V4__add_user_phone.sql
ALTER TABLE flyway_demo_user
    ADD COLUMN phone VARCHAR(32) NULL COMMENT '手机号' AFTER email;
```

重启应用即可，Flyway 只会跑未执行过的 `V4`。

---

## 5. 关键配置说明

配置位于 `application-base.yml` 的 `spring.flyway`：

| 配置项 | 本模块取值 | 含义 |
|--------|------------|------|
| `enabled` | `true` | 是否启用 Flyway |
| `locations` | `classpath:db/migration` | 脚本扫描路径 |
| `baseline-on-migrate` | `true` | 已有库首次接入时自动 baseline |
| `baseline-version` | `0` | baseline 版本号 |
| `validate-on-migrate` | `true` | 迁移前校验已执行脚本是否被篡改 |
| `encoding` | `UTF-8` | 脚本编码 |
| `table` | `flyway_schema_history` | 历史记录表名 |

`application.yml` 中 `spring.isEnable: false`：关闭公共模块字典初始化（本库无 `global_dict`）。

---

## 6. 代码结构

```text
flywayModel
├── controller/FlywayDemoController.java   # 验证接口
├── entity/
│   ├── FlywayDemoUser.java                # 演示用户
│   └── FlywayHistoryRecord.java           # 迁移历史
├── mapper/                                # BaseMapper
├── service/ + impl/                       # IService / ServiceImpl
├── FlywayDemoService.java                 # 汇总查询
└── resources/db/migration/                # SQL 脚本
```

查询走 MyBatis-Plus，不直接写 JdbcTemplate SQL。

---

## 7. 常见问题

### 7.1 Unsupported Database: MySQL 8.0

缺少 `flyway-mysql` 依赖。本模块 `pom.xml` 已引入，其他模块复用时记得带上。

### 7.2 Validate failed / Checksum mismatch

已执行脚本被改过。处理方式（选一）：

1. **推荐**：不要改旧脚本，新增更高版本脚本
2. 开发环境可清库 / 删 `flyway_schema_history` 后重跑（会丢迁移记录，谨慎）
3. 使用 Flyway repair（生产需评估）

### 7.3 脚本执行了但接口查不到表

确认连的是 `flyway_dev`，且启动日志里迁移成功；再用 `/flyway/history` 看是否有对应 version。

### 7.4 想临时关闭自动迁移

```yaml
spring:
  flyway:
    enabled: false
```

---

## 8. 和 mybatisModel 的关系

| | mybatisModel | flywayModel |
|--|-------------|-------------|
| 职责 | 业务 CRUD / 多数据源演示 | Schema 版本迁移演示 |
| 库 | `mybatis_dev` 等 | `flyway_dev` |
| 表结构来源 | 手工 SQL（`resources/sql`） | Flyway 版本脚本 |

后续若要把业务库也纳入版本管理，可把脚本目录与 Flyway 配置迁到对应业务模块，或把公共脚本抽成依赖 jar。
