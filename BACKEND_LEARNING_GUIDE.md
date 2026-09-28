# IndividualProject 后端学习指南

## 1. 项目定位

`IndividualProject` 是一个基于 **Java 21、Spring Boot 3.5.3 和 Maven** 的多模块后端工程。

它不是围绕用户、订单、支付等业务构建的常规单体应用，而更接近一个：

> 后端技术示例库、个人实验项目和可复用脚手架集合。

根目录的 `pom.xml` 是聚合 POM，本身不能作为服务启动。真正可运行的是各个业务或实验子模块；多数模块依赖 `commonModel`，并可以独立启动。

项目适合用来学习 Spring Boot 后端技术，但不应未经整理就直接作为生产项目模板。

---

## 2. 技术栈概览

| 类别 | 技术 |
| --- | --- |
| 基础环境 | Java 21、Maven |
| Web 框架 | Spring Boot 3.5.3、Spring MVC |
| 数据访问 | MyBatis-Plus、动态数据源、Druid |
| 关系型数据库 | MySQL |
| 缓存与分布式能力 | Redis、Redisson、Lua |
| 文档数据库 | MongoDB |
| 权限认证 | Spring Security、JWT、Sa-Token |
| 消息队列 | RabbitMQ、RocketMQ |
| 数据库迁移 | Flyway |
| 网络通信 | Netty、WebSocket |
| 大数据组件 | HBase |
| 文件存储 | 本地文件、FTP、MinIO、阿里云 OSS、华为云 OBS、S3 |
| 数据处理 | EasyExcel、Easy-Trans、Fastjson2 |
| 并发与事件 | CompletableFuture、虚拟线程、Spring Event、Disruptor |
| 音视频 | JavaCV、FFmpeg |

---

## 3. 模块地图

项目共有 18 个 Maven 子模块。

| 模块 | 主要内容 | 学习优先级 |
| --- | --- | --- |
| `commonModel` | 统一响应、全局异常、自动配置、MyBatis 公共能力 | 最高 |
| `mybatisModel` | MyBatis-Plus、分页、XML 查询、关联查询、动态数据源 | 最高 |
| `springSecurityModel` | Spring Security、JWT、角色权限、认证过滤器 | 高 |
| `redisModel` | Redis、Redisson、Lua、分布式锁、限流算法 | 高 |
| `flywayModel` | 数据库版本管理与 SQL 迁移 | 高 |
| `rabbitMqModel` | Direct、Topic、Fanout、订单消息等 | 高 |
| `rocketClientModel` | RocketMQ 原生客户端 | 中 |
| `rocketSpringModel` | RocketMQ 与 Spring Boot 集成 | 中 |
| `mongodbModel` | MongoDB CRUD、分页、聚合和局部更新 | 中 |
| `fileStorageModel` | 多种本地及对象存储实现 | 中 |
| `easyTransModel` | 字典、枚举和关联字段自动翻译 | 中 |
| `saTokenModel` | Sa-Token 登录和会话示例 | 中 |
| `nettyModel` | Netty 服务端、客户端和通信处理 | 进阶 |
| `logViewModel` | 使用 WebSocket 实时查看日志 | 进阶 |
| `hbaseModel` | HBase 与 RocketMQ 相关示例 | 进阶 |
| `networkModel` | 数据查询、同步和数据源相关实验 | 进阶 |
| `contrastModel` | Excel、文件、视频、事件、多线程、BitSet 等综合实验 | 按需 |
| `annotationModel` | 自定义注解和公共能力接入实验 | 按需 |

### 3.1 公共基础模块

#### `commonModel`

这是理解整个工程的关键模块，提供：

- `GlobalResult`：统一接口返回结构；
- `GlobalResponseBodyAdvice`：自动包装 Controller 返回值；
- `GlobalExceptionAdvice`：统一异常处理；
- `GlobalException`：自定义业务异常；
- `GlobalNoWrap`：让指定接口跳过统一包装；
- `GlobalMyBatisPlusConfiguration`：MyBatis-Plus 公共配置；
- `GlobalSqlPrintInterceptor`：SQL 打印拦截器；
- Spring Boot 自动配置注册。

公共组件通过下面的文件自动加载：

```text
commonModel/src/main/resources/META-INF/spring/
└── org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

这部分可以用来学习 Spring Boot 自定义 Starter 和自动配置机制。

#### `fileStorageModel`

该模块主要是可复用的文件存储组件，而不是一个独立 Web 业务服务，支持：

- 本地文件；
- FTP；
- MinIO；
- 阿里云 OSS；
- 华为云 OBS；
- AWS S3 兼容协议。

---

## 4. 典型项目结构

多数可运行模块采用以下分层：

```text
模块目录
├── pom.xml
└── src
    ├── main
    │   ├── java/com/gqtian/...
    │   │   ├── XxxApplication.java
    │   │   ├── config
    │   │   ├── controller
    │   │   ├── service
    │   │   │   └── impl
    │   │   ├── mapper
    │   │   ├── entity
    │   │   ├── vo / dto / params
    │   │   ├── constant
    │   │   └── utils
    │   └── resources
    │       ├── application.yml
    │       ├── application-base.yml
    │       ├── application-custom.yml
    │       ├── mapper
    │       └── sql
    └── test
```

各层常见职责如下：

| 层 | 职责 |
| --- | --- |
| Controller | 接收 HTTP 请求、解析参数、返回结果 |
| Service | 实现业务规则、事务和流程编排 |
| Mapper / Repository | 访问 MySQL、MongoDB 等数据源 |
| Entity | 映射数据库表或持久化文档 |
| DTO / Params | 接收客户端提交的数据 |
| VO | 定义返回给客户端的数据结构 |
| Config | 装配 Spring Bean 和第三方组件 |
| Interceptor / Filter / Aspect | 处理认证、限流、日志等横切逻辑 |

---

## 5. 一次请求如何执行

并非所有模块都会经过下面的全部组件，但完整请求链路可以理解为：

```text
客户端 HTTP 请求
        ↓
Servlet Filter
例如：JWT 解析和身份认证
        ↓
Spring MVC Interceptor
例如：Redis 接口限流
        ↓
Controller
接收路径、查询参数或 JSON 请求体
        ↓
Service
业务逻辑、事务、数据组合
        ↓
Mapper / Repository
        ↓
MySQL / Redis / MongoDB / MQ
        ↓
ResponseBodyAdvice
统一包装返回值
        ↓
JSON 响应
```

如果执行过程中抛出异常，则通常进入：

```text
异常
  ↓
GlobalExceptionAdvice
  ↓
统一错误响应
```

---

## 6. 项目中的中间件与横切组件

“中间件”在本项目中可以分为基础设施和请求处理组件两类。

### 6.1 基础设施中间件

- MySQL：关系型业务数据；
- Redis：缓存、计数、限流、分布式锁；
- MongoDB：文档型数据和聚合查询；
- RabbitMQ：异步消息和服务解耦；
- RocketMQ：消息生产、消费和高级消息场景；
- HBase：大规模列式数据存储；
- MinIO、OSS、OBS、S3、FTP：文件和对象存储；
- Flyway：数据库结构版本管理。

这些模块通常需要先准备相应的本地服务或 Docker 容器，然后修改本地连接配置。

### 6.2 统一响应处理

`GlobalResponseBodyAdvice` 实现了 `ResponseBodyAdvice<Object>`，负责将普通 Controller 返回值包装为统一结构。

示意结果：

```json
{
  "code": "100",
  "message": "操作成功",
  "data": {}
}
```

如果方法标注 `@GlobalNoWrap`，则保持原始响应，不进行包装。

### 6.3 全局异常处理

`GlobalExceptionAdvice` 使用 `@RestControllerAdvice` 和 `@ExceptionHandler` 处理：

- 自定义业务异常；
- `@Valid` 参数校验异常；
- 参数绑定失败；
- 缺少请求参数；
- 参数类型错误；
- JSON 请求体解析错误；
- HTTP 请求方法错误；
- 未知系统异常。

### 6.4 Redis 限流

`redisModel` 中实现了四种限流算法：

- 固定窗口；
- 滑动窗口；
- 令牌桶；
- 漏桶。

`MyWebConfig` 当前注册的是固定窗口限流拦截器，其他实现保留为可切换示例。

项目还提供了 `@RequestLimitAop` 和 `RequestLimitAspect`，用于学习“自定义注解 + AOP + Redis”的声明式限流。

### 6.5 JWT 认证过滤器

`springSecurityModel` 中的 `JwtAuthenticationFilter` 继承 `OncePerRequestFilter`，负责：

1. 从请求头读取 Token；
2. 验证并解析 JWT；
3. 加载用户信息；
4. 将认证结果写入 `SecurityContext`；
5. 将请求交给后续过滤器和 Controller。

---

## 7. 代表性示例

### 7.1 MyBatis-Plus 示例

`mybatisModel` 的 `BookController` 展示了：

- Lambda 条件构造器；
- 分页查询；
- XML 自定义 SQL；
- 嵌套查询；
- 嵌套结果映射；
- 主从数据源查询；
- Controller、Service、Mapper、Entity、VO 分层。

适合用它理解最基本的后端数据链路：

```text
Controller → Service → Mapper → MySQL
```

### 7.2 Spring Security 与 JWT

`springSecurityModel` 包含：

- 用户名密码登录；
- BCrypt 密码校验；
- JWT 签发与解析；
- 无状态认证；
- USER、ADMIN 角色；
- URL 级权限；
- `@PreAuthorize` 方法级权限；
- 401 未认证处理；
- 403 无权限处理。

主要权限规则为：

| 路径 | 权限 |
| --- | --- |
| `/auth/login` | 匿名访问 |
| `/test/public/**` | 匿名访问 |
| `/admin/**` | ADMIN |
| `/user/**` | USER 或 ADMIN |
| 其他接口 | 必须登录 |

### 7.3 Redis 示例

`redisModel` 包含：

- 常用 Redis 数据操作；
- Geo 地理位置；
- Lua 脚本；
- Redisson 分布式锁；
- 四种接口限流算法；
- 注解式接口限流。

### 7.4 MongoDB 示例

`mongodbModel` 包含：

- 新增和批量新增；
- 根据 ID、用户名查询；
- 列表和分页查询；
- 条件搜索；
- 全量、局部更新；
- 数组标签增删；
- 批量状态更新；
- 条件删除；
- 城市聚合统计。

### 7.5 消息队列示例

`rabbitMqModel` 适合作为消息队列入门模块，包含：

- Direct Exchange；
- Topic Exchange；
- Fanout Exchange；
- 订单消息；
- 消费者监听；
- 死信或延迟相关场景。

学习 RabbitMQ 后，可以继续对比：

- `rocketSpringModel`：Spring 风格的 RocketMQ 集成；
- `rocketClientModel`：RocketMQ 原生客户端 API。

### 7.6 综合技术示例

`contrastModel` 包含较多相互独立的示例：

- EasyExcel 导入导出；
- 文件上传、下载和预览；
- JSON 与文件混合上传；
- JavaCV、FFmpeg 视频截图；
- Spring 事件监听；
- Disruptor；
- CompletableFuture；
- Java 虚拟线程；
- BitSet 和布隆过滤器；
- Jackson 与 Fastjson 注解对比。

该模块适合按功能查阅，不适合作为第一条学习主线。

---

## 8. 配置文件说明

多数模块使用三层配置：

```text
application.yml
application-base.yml
application-custom.yml
```

### `application.yml`

通常包含：

- 服务端口；
- Context Path；
- 应用名称；
- 激活的 Profile；
- 日志配置；
- 导入自定义配置。

### `application-base.yml`

通常包含基础设施连接信息，例如：

- MySQL；
- Redis；
- MongoDB；
- RabbitMQ；
- Flyway。

### `application-custom.yml`

适合放置开发者自己的覆盖配置。

建议进一步调整为环境变量形式：

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:mysql://localhost:3306/demo}
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:}
```

---

## 9. 如何运行项目

### 9.1 环境要求

- JDK 21；
- Maven 3.9 或兼容版本；
- IntelliJ IDEA（可选）；
- MySQL、Redis、MongoDB、MQ 等按模块准备。

检查环境：

```powershell
java -version
mvn -version
```

### 9.2 编译整个项目

```powershell
mvn clean compile
```

完整打包：

```powershell
mvn clean package
```

部分模块需要外部中间件才能通过上下文测试。如果只希望先编译和打包，可以暂时跳过测试：

```powershell
mvn clean package -DskipTests
```

### 9.3 编译指定模块及其依赖

例如编译 Spring Security 模块及 `commonModel`：

```powershell
mvn -pl springSecurityModel -am test -DskipTests
```

参数含义：

- `-pl springSecurityModel`：只选择指定模块；
- `-am`：同时构建它依赖的本项目模块；
- `-DskipTests`：编译测试代码但不执行测试。

### 9.4 推荐的第一个运行模块

建议先运行 `springSecurityModel`。它使用内存用户并排除了数据库自动配置，不需要先安装 MySQL。

先安装模块依赖：

```powershell
mvn -pl springSecurityModel -am install -DskipTests
```

再启动：

```powershell
mvn -f springSecurityModel/pom.xml spring-boot:run
```

也可以在 IDE 中运行：

```text
com.gqtian.security.SpringSecurityModelApplication
```

默认访问地址：

```text
http://localhost:2000/springSecurityModel
```

### 9.5 测试公开接口

```powershell
Invoke-RestMethod `
  http://localhost:2000/springSecurityModel/test/public/hello
```

### 9.6 登录并访问受保护接口

该模块内置演示用户 `user` 和 `admin`，示例密码均为 `123456`。

```powershell
$body = @{
    username = "user"
    password = "123456"
} | ConvertTo-Json

$login = Invoke-RestMethod `
    -Method Post `
    -Uri "http://localhost:2000/springSecurityModel/auth/login" `
    -ContentType "application/json" `
    -Body $body

Invoke-RestMethod `
    -Uri "http://localhost:2000/springSecurityModel/auth/me" `
    -Headers @{
        Authorization = "Bearer $($login.token)"
    }
```

### 9.7 启动其他模块时的注意事项

- 多数模块默认使用端口 `2000`，不能以默认配置同时运行；
- 每个模块通常配置了独立的 `context-path`；
- 数据库模块启动前需要创建数据库或执行对应 SQL；
- Redis、MongoDB、RabbitMQ、RocketMQ 等模块需要先启动相应服务；
- 不要直接连接仓库配置中原有的远程环境；
- 推荐为本机单独创建 `application-local.yml`。

---

## 10. 推荐学习路线

### 第一阶段：Spring Boot 和 HTTP 基础

目标：理解一个 HTTP 请求如何进入后端并返回 JSON。

建议顺序：

1. 启动 `springSecurityModel`；
2. 阅读一个公开 Controller；
3. 理解 `@RestController` 和 `@RequestMapping`；
4. 学习查询参数和 JSON 请求体；
5. 学习依赖注入；
6. 阅读 `GlobalResponseBodyAdvice`；
7. 阅读 `GlobalExceptionAdvice`；
8. 理解 `application.yml` 和 Profile。

### 第二阶段：数据库 CRUD

以 `mybatisModel` 为主线：

1. 数据库表与 Entity；
2. Mapper；
3. Service；
4. Controller；
5. MyBatis-Plus 条件构造器；
6. 分页；
7. XML SQL；
8. 一对一和一对多映射；
9. 事务；
10. 动态数据源。

完成 CRUD 后，继续用 `flywayModel` 学习数据库迁移。

### 第三阶段：缓存和并发控制

以 `redisModel` 为主：

1. Redis 基本数据类型；
2. 缓存读写和过期；
3. 缓存穿透、击穿、雪崩；
4. Redisson 分布式锁；
5. Lua 原子操作；
6. 固定窗口限流；
7. 滑动窗口限流；
8. 令牌桶和漏桶。

### 第四阶段：认证和授权

深入 `springSecurityModel`，然后与 `saTokenModel` 对比：

- Filter 与 Interceptor 的区别；
- Session 与 JWT 的区别；
- 认证与授权的区别；
- 401 与 403；
- URL 权限与方法权限；
- `SecurityContext`；
- Token 过期、续签和注销。

### 第五阶段：异步和消息队列

建议顺序：

```text
Spring Event
    ↓
RabbitMQ
    ↓
RocketMQ Spring
    ↓
RocketMQ 原生客户端
    ↓
Disruptor
```

### 第六阶段：专项能力

根据需求选择：

- MongoDB；
- 文件和对象存储；
- WebSocket；
- Netty；
- HBase；
- Easy-Trans；
- Excel 和视频处理。

推荐的完整主线是：

```text
springSecurityModel 简单 HTTP 接口
    ↓
commonModel 公共处理
    ↓
mybatisModel 数据访问
    ↓
flywayModel 数据库迁移
    ↓
redisModel 缓存与限流
    ↓
springSecurityModel 完整认证授权
    ↓
rabbitMqModel 消息队列
    ↓
其他专项模块
```

---

## 11. 与常规生产项目的差异

这个项目覆盖面很广，但存在一些示例项目特征：

- 模块之间比较独立，缺少统一的业务领域；
- 很多 Controller 使用宽泛的 `@RequestMapping`，没有严格限定 HTTP 方法；
- 自动化测试较少，很多测试只是 Spring Context 启动测试；
- 多个模块默认使用同一个端口；
- `contrastModel` 内容较杂，更像示例集合；
- `commonModel` 同时承载 Web、数据库和工具能力，生产中通常会进一步拆分；
- 部分示例关注技术效果，没有覆盖幂等、审计、监控和完整事务设计；
- 根 README 主要描述 Linux 部署脚本，并非完整的开发指南。

因此，学习时应该提取其中的技术点，并逐步建立自己的标准项目结构。

---

## 12. 安全注意事项

仓库中的部分配置文件包含外网地址、明文账号密码或默认 JWT 密钥。即使这些值只是测试配置，也不应该继续用于任何实际环境。

建议立即进行以下处理：

1. 将已经提交过的数据库、Redis、RabbitMQ 等密码视为已泄露并轮换；
2. 将密码和密钥改为环境变量；
3. 仓库只保留 `application-example.yml`；
4. 将真实本地配置加入 `.gitignore`；
5. 不要在日志中打印 Token、密码和完整敏感请求；
6. 生产环境使用足够长的随机 JWT 密钥；
7. 不要直接使用仓库中的远程数据库进行学习实验。

环境变量示例：

```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

security:
  jwt:
    secret: ${JWT_SECRET}
```

---

## 13. 建议的练习任务

可以按照以下顺序动手改造项目：

1. 新增一个简单的 `HelloController`；
2. 新增图书的创建、修改和删除接口；
3. 为新增图书接口加入 `@Valid` 参数校验；
4. 抛出并处理一个自定义业务异常；
5. 使用 Flyway 新增一张表；
6. 为图书详情添加 Redis 缓存；
7. 解决缓存穿透问题；
8. 为写接口添加分布式锁或幂等处理；
9. 为接口加入 JWT 登录校验；
10. 增加 ADMIN 和 USER 权限差异；
11. 创建图书后发送 RabbitMQ 消息；
12. 编写单元测试和接口集成测试；
13. 使用 Docker Compose 启动 MySQL、Redis 和 RabbitMQ；
14. 将所有敏感配置迁移到环境变量。

完成这些练习后，就能把项目中的零散技术示例串联成一套比较完整的后端开发流程。

