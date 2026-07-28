# RabbitMQ 学习注意

> 基于本模块（`rabbitMqModel`）实现与会话讨论整理，侧重概念、拓扑、广播与和 RocketMQ 的对比。  
> 代码入口：`com.gqtian.rabbit.config.RabbitMqConfig`、生产者 / 消费者包。

---

## 1. RabbitMQ 是什么

RabbitMQ 是基于 **AMQP** 的消息中间件。核心思路：

> 生产者不直接把消息丢给消费者，而是发给 **交换机（Exchange）**，由交换机按 **绑定（Binding）** 规则投到 **队列（Queue）**，消费者再从队列消费。

典型用途：业务解耦、异步处理、削峰填谷、广播通知、死信与延迟等。

---

## 2. 核心概念与属性

### 2.1 核心实体

| 概念 | 含义 | 常见属性 |
|------|------|----------|
| **Producer** | 生产者 | 目标 Exchange、routingKey、消息体、持久化等 |
| **Exchange** | 交换机，负责路由，一般不存业务消息 | 名称、类型、durable、autoDelete |
| **Queue** | 队列，消息真正存放处 | 名称、durable、exclusive、autoDelete、arguments（DLX、TTL 等） |
| **Binding** | 交换机 ↔ 队列的绑定 | 绑定键（binding key）/ 匹配规则 |
| **Routing Key** | 发送时消息自带的路由标签 | 字符串，与绑定键匹配 |
| **Consumer** | 消费者 | 订阅队列、ACK 模式、prefetch、并发 |
| **Connection / Channel** | 连接与信道 | 一连接多 Channel |
| **Virtual Host** | 虚拟主机 | 多租户隔离 |
| **Message** | 消息 | body、deliveryMode、expiration、headers 等 |

### 2.2 交换机类型（路由模型）

| 类型 | 规则 |
|------|------|
| **Direct** | 发送 routingKey 与绑定键 **精确相等** |
| **Topic** | 绑定键支持通配：`*` 一段，`#` 多段 |
| **Fanout** | 广播，**忽略** routingKey |
| **Headers** | 按消息头匹配（较少用） |

### 2.3 可靠性相关

| 能力 | 说明 |
|------|------|
| 持久化 | 队列 durable + 消息 PERSISTENT（Spring `convertAndSend` 默认多为 PERSISTENT） |
| Publisher Confirm | Broker 确认是否接收（本模块 `publisher-confirm-type: correlated`） |
| Mandatory / Return | 路由不到队列时回退生产者 |
| ACK | 自动 / 手动；nack + requeue 或进死信 |
| Prefetch | 未 ACK 前最多推多少条 |
| TTL | 消息 / 队列存活时间 |
| DLX / DLQ | 死信交换机 / 死信队列 |
| 延迟 | 插件，或 **TTL + 无消费者延迟队列 + 死信转发**（本模块 delay 方案） |

---

## 3. 初始化要准备什么

使用前需要在 Broker 上准备好拓扑，本质是三样：

1. **交换机 Exchange**  
2. **队列 Queue**  
3. **二者的绑定 Binding**

少任何一环，消息可能发不出去、进不了队、或消费者收不到。

| 只初始化了 | 结果 |
|------------|------|
| 只有 Exchange | 无绑定则进不了队列（可能被丢，或 mandatory 回退） |
| 只有 Queue | 发到自定义 Exchange 时对不上路由 |
| 有 Exchange + Queue，无 Binding | 两边存在但不通 |
| 三样齐全 | `convertAndSend(exchange, routingKey, msg)` 可按规则进队 |

本模块在 `RabbitMqConfig` 中用 `@Bean` 声明；**应用启动时** Spring AMQP 自动 declare 到 RabbitMQ。

- 已存在且参数一致 → 复用  
- 已存在但参数不一致（如后来加了 DLX）→ 可能声明失败，需先删旧队列再启  

拓扑按场景成组阅读更清晰：**同一场景下「交换机 → 队列 → 绑定」放在一起**（本模块已按此组织）。

---

## 4. `new Queue` 与 `BindingBuilder.bind`

### 4.1 `new Queue`：声明队列

```java
// 持久化队列
new Queue(name, true);

// 带参数（如死信）
new Queue(name, durable, exclusive, autoDelete, arguments);
```

| 参数 | 含义 |
|------|------|
| name | 队列名，VHost 内唯一 |
| durable | 重启后队列定义是否保留 |
| exclusive | 仅当前连接可用 |
| autoDelete | 无消费者后是否自动删除 |
| arguments | 扩展：死信交换机、TTL 等 |

可理解为：**在 Broker 上建（或确认）一个存放消息的邮箱。**

### 4.2 `BindingBuilder.bind`：声明绑定

```java
BindingBuilder.bind(队列).to(交换机).with(绑定键);  // Direct / Topic
BindingBuilder.bind(队列).to(fanout交换机);         // Fanout 无需 with
```

可理解为：**给邮箱登记分拣规则——从哪个交换机、按什么键收信。**

消息路径：

```text
生产者 convertAndSend(exchange, routingKey, msg)
  → 交换机按 Binding 匹配
  → 队列
  → @RabbitListener(queues = "...")
```

消费者只关心 **队列名**，不负责在监听方法里写 Binding。

### 4.3 示例：直连字符串绑定对应谁

```java
@Bean
public Binding directBindingString(Queue directQueueString, DirectExchange directExchange) {
    return BindingBuilder.bind(directQueueString).to(directExchange).with(ROUTING_KEY_DIRECT_STRING);
}
```

Spring 按 **参数名** 注入同名 Bean：

| 角色 | 实际名称 |
|------|----------|
| 队列 | `rabbit.direct.queue.string` |
| 交换机 | `rabbit.direct.exchange` |
| 绑定键 | `direct.routing.key.string` |

发送时 routingKey 必须与 `.with(...)` 一致（Direct 精确匹配）才能进该队列。

---

## 5. 绑定上的 routingKey 是干什么的

绑定时的 key 更准确叫 **binding key（绑定键）**，表示：**什么样的消息可以进入这个队列**。

| 时机 | 名称 | 谁提供 | 作用 |
|------|------|--------|------|
| 发送 | routing key | `convertAndSend(ex, routingKey, msg)` | 消息上的路由标签 |
| 绑定 | binding key | `.with(xxx)` | 队列登记的匹配条件 |

交换机用两者做匹配，成功则入队。

| 交换机 | `.with(key)` | 匹配方式 |
|--------|--------------|----------|
| Direct | 需要 | 全等 |
| Topic | 需要 | 通配（如 `order.#`） |
| Fanout | 不需要 | 忽略 key，绑上即收 |

注意：

- **绑定键不必等于队列名**（自定义交换机时）  
- **默认交换机**（名为 `""`）例外：routingKey **必须等于队列名**  

---

## 6. 同一队列能否绑多个交换机

**可以。** Binding 是「交换机 → 队列」的规则，队列不独占某一个交换机。

```text
Exchange A ──binding──► Queue X
Exchange B ──binding──► Queue X
```

谁路由命中，消息就进 Queue X。  
反过来：一个交换机绑多个队列更常见。

注意：多路汇入同一队列后，消费端要能处理多种来源/格式（可看 headers、业务字段）。

---

## 7. 不指定交换机和绑定可以吗

**可以。** 每个 VHost 有 **默认交换机**（名字为空字符串 `""`，类型 Direct）：

- routingKey **等于队列名** 时，消息进入该队列  
- 相当于每个队列有一条隐式绑定  

最少只需声明队列，例如：

```java
rabbitTemplate.convertAndSend("", "hello.queue", "hello");
// 或部分重载默认走默认交换机
```

| | 默认交换机 | 自建 Exchange + Binding |
|--|------------|-------------------------|
| 声明成本 | 通常只要 Queue | Exchange + Queue + Binding |
| 路由 | key = 队列名 | Direct / Topic / Fanout 等 |
| 广播 / 复杂路由 | 不方便 | 自然支持 |
| 解耦 | 生产者要知道队列名 | 生产者只认交换机 + routingKey |

简单点对点可用默认交换机；本模块的主题、扇出、死信、延迟等 **必须** 自建拓扑。

---

## 8. 过滤发生在哪（理解模型的关键）

| | RabbitMQ | RocketMQ（对照） |
|--|----------|------------------|
| 过滤时机 | 主要在 **Exchange → Queue 分发时** | 主要在 **消费订阅**（Tag / SQL 等） |
| 生产 | 带 routingKey，参与路由 | 写入 Topic（可带 Tag，多打标而非筛消费者） |
| 进队后 | 队列里是什么，消费者通常直接消费 | Topic 上更“全”，各组按订阅过滤取用 |

更严谨的表述：

- RabbitMQ：**主要在分发进队时路由/过滤**；消费端仍可业务 `if`，但产品重心在路由。  
- RocketMQ：**生产负责写入 Topic**；**按谁消费、消费什么在订阅链路过滤**（可为 Broker 侧或客户端过滤）。

---

## 9. 广播消费

### 9.1 RabbitMQ 能否广播

**能。** 标准做法是 **Fanout 交换机**：

```text
Producer ──发一次──► Fanout Exchange
                        ├─ Queue A → Consumer A
                        └─ Queue B → Consumer B
```

- 生产者 **只发一次**（routingKey 可传 `""`）  
- Broker 按绑定 **复制到多个队列**  
- 每个队列一份 → 各消费者各处理一份  

本模块 `fanoutExchange` + `fanoutQueueA/B` + 绑定 + `FanoutMessageConsumer` 即广播示例。

其他「一人一份」方式：Topic/Direct 把同一规则绑到多个队列；或每客户端临时队列绑 Fanout。

### 9.2 广播 vs 竞争

| 模式 | 配置 | 效果 |
|------|------|------|
| **竞争** | 多个消费者听 **同一个队列名** | 一条消息只被其中一个处理 |
| **广播** | **多个不同队列名** 绑同一 Fanout | 每个队列各收一份 |

**广播时队列名必须互不相同**（同名 = 同一队列 = 变竞争）。

### 9.3 四个消费节点、一个生产者

| 目标 | 生产者 | 队列 |
|------|--------|------|
| 4 节点 **分担**（一条只处理一次） | **发 1 次** | **1 个队列**，4 节点同队列名 |
| 4 节点 **各收一份**（广播） | 仍 **发 1 次** 到 Fanout | **4 个不同队列**，各绑 Fanout |

**两种都不必**让生产者对 4 个队列各 `send` 一遍（Fanout 由 Broker 复制）。

### 9.4 新增消费节点要不要改生产者

| 场景 | 生产者 | 其他 |
|------|--------|------|
| 竞争扩容 | **不用改** | 新节点听同一队列 |
| Fanout 广播扩容 | **通常不用改** | 新队列 + 绑定 + 新节点监听 |
| 生产者写死往 Q1..Qn 各发一次 | 要改（应避免这种写法） | — |

广播扩节点：**消费侧（或运维）建队列并绑定到已有 Fanout**，生产者继续 `convertAndSend(FANOUT, "", msg)` 即可。

### 9.5 队列名防重

同一 VHost 队列名全局唯一：

- 重名且参数相同 → 复用同一队列 → 易变成竞争  
- 重名且参数不同 → 声明失败  

不必只靠人肉：可用 `业务.fanout.服务名`、加实例 ID/UUID，或 exclusive/匿名临时队列。

### 9.6 生产者、消费者分属两个独立项目（广播）

| 角色 | 建议声明 |
|------|----------|
| **生产者项目** | **同一个 Fanout 交换机**（名称、类型、durable 等与约定一致）+ 发送；**一般不建**各消费队列 |
| **每个消费项目** | **同一个 Fanout 交换机** + **自己的队列**（名唯一）+ **绑定到该交换机** + Listener |

- 交换机名/类型要一致（可公共常量、配置中心、文档约定）  
- 多方都可以 declare 同一交换机（幂等；参数不一致会报错）  
- 加订阅方：**主要加/改消费项目**，不改生产者发送逻辑  

```text
[生产者] → Fanout: order.fanout
                ├─ bind → queue-service-a → [消费者A项目]
                └─ bind → queue-service-b → [消费者B项目]
```

---

## 10. 本模块拓扑速览（对照代码）

`RabbitMqConfig` 按场景分组：

| 场景 | 交换机 | 队列 | 绑定要点 |
|------|--------|------|----------|
| 直连 | `rabbit.direct.exchange` | 字符串 / 对象 两队列 | 不同 routingKey；对象队列带 DLX |
| 主题 | `rabbit.topic.exchange` | `rabbit.topic.queue` | `order.#` |
| 扇出（广播） | `rabbit.fanout.exchange` | A、B 两队列 | 无 binding key |
| 延迟 | `rabbit.delay.exchange` | 无消费者延迟队列 | TTL 到期死信转发到直连字符串队列 |
| 死信 | `rabbit.dlx.exchange` | `rabbit.dlx.queue` | 承接对象队列 nack 等 |

调试接口前缀（context-path 以 yml 为准）：`/rabbit/*`（direct、order、topic、fanout、delay、confirm 等）。

### 实践注意（本模块踩坑点摘要）

1. **死信消费**建议用原始 `Message` 读 body，避免 JSON 对象被强转 String 失败。  
2. **仅配置了 DLX 的队列**，`nack(requeue=false)` 才会进死信；未配 DLX 则直接丢弃。  
3. **延迟**不要用「往有消费者的业务队列设 expiration」；应用 **延迟队列 + TTL + 死信转发**。  
4. 发送字符串时 routingKey 要与 **字符串队列** 绑定一致，勿误发到对象队列路由键。  
5. 队列参数变更后若与 Broker 上旧定义冲突，需删除旧队列再启动。

---

## 11. 与 RocketMQ 的区别（本次会话总结）

### 11.1 模型中心

| | RabbitMQ | RocketMQ |
|--|----------|----------|
| 中心 | **Exchange + Binding + Queue** | **Topic + MessageQueue + ConsumerGroup** |
| 协议 | AMQP 为主 | 自有协议 + 多语言客户端 |
| 路由 | 交换机类型丰富，绑定时定规则 | Topic 写入；Tag/SQL 等在订阅侧过滤 |

### 11.2 过滤时机（会话共识）

| | RabbitMQ | RocketMQ |
|--|----------|----------|
| 生产 | routingKey 参与 **路由决策** | 写入 Topic，Tag 多为 **打标** |
| 过滤重心 | **分发到队列时**（Exchange 匹配 Binding） | **消费订阅时** |
| 队列/主题内 | 进队结果已是路由后的子集 | Topic 更全，各组各取所需 |

### 11.3 广播与扩容

| | RabbitMQ | RocketMQ |
|--|----------|----------|
| 广播 | Fanout + **多队列多绑定**；生产者通常发一次 | 消费组广播模式等；组内实例语义由产品定义 |
| 竞争扩容 | 多节点 **同队列名**，生产者不改 | 同 ConsumerGroup 内负载均衡 |
| 广播加订阅方 | 新队列 + 绑同一 Fanout，**一般不改生产者** | 视模式增加订阅/实例，生产者多仍只发 Topic |

### 11.4 能力侧重（选型印象）

| 维度 | RabbitMQ | RocketMQ |
|------|----------|----------|
| 强项 | 灵活路由、多交换类型、标准协议、死信/工作流类场景 | 高吞吐、堆积、顺序消息、事务消息、延迟更“开箱” |
| 顺序 / 事务 | 可做但要自己约束 | 产品级顺序、半消息事务更体系化 |
| 学习曲线 | Exchange/Binding 概念多 | Topic/Group/Offset 一条线，集群概念也不少 |

### 11.5 术语粗映射（帮助切换脑图）

| 维度 | RabbitMQ | RocketMQ |
|------|----------|----------|
| 入口 | Exchange + routingKey | Topic（+ Tag） |
| 存放 | Queue | MessageQueue（分片） |
| 规则 | Binding | 订阅 Tag/SQL、队列选择 |
| 消费隔离 | 多队列 / 多绑定；同队列竞争 | ConsumerGroup（集群负载 / 广播） |
| 死信 | DLX/DLQ | 重试队列 + %DLQ% 等 |
| 进度 | 队列内投递 + ACK | Offset |

### 11.6 一句话对照

- **RabbitMQ 的灵魂**：「交换机 + 绑定」的灵活路由；广播靠多队列，过滤多在进队前。  
- **RocketMQ 的灵魂**：「Topic + 消费组」的吞吐与订阅语义；过滤多在订阅侧，顺序/事务/堆积更突出。  

两者都是消息中间件，都有持久化、确认、重试、死信等可靠性手段，但 **路由模型、消费模型、擅长场景** 不同；本模块代码走的是 **Rabbit 的 Exchange/Queue/Binding** 路线。

---

## 12. 推荐记忆口诀

1. **拓扑三件套**：交换机、队列、绑定（默认交换机可省略后两者声明，能力也变弱）。  
2. **发送认交换机 + key，消费认队列。**  
3. **竞争 = 同队列；广播 = 多不同队列 + Fanout（生产者仍发一次）。**  
4. **扩消费者优先改消费侧拓扑，别让生产者写死队列列表。**  
5. **绑定键是匹配规则，不一定等于队列名。**  
6. **和 Rocket 比：Rabbit 进队前路由，Rocket 订阅时过滤。**

---

## 13. 相关代码索引

| 内容 | 路径 |
|------|------|
| 拓扑与客户端配置 | `src/main/java/com/gqtian/rabbit/config/RabbitMqConfig.java` |
| 生产者 | `.../producer/RabbitProducerService.java` |
| HTTP 调试 | `.../controller/RabbitProducerController.java` |
| 直连 / 主题 / 扇出 / 死信消费者 | `.../consumer/*` |
| 订单消息体 | `.../message/OrderMessage.java` |
| Rabbit 连接与监听 | `src/main/resources/application-base.yml` |

---

*文档随模块演进可继续补充；与 Broker 管理台对照 Exchange / Queue / Bindings 更直观。*
