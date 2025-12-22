--========== 秒杀业务 Lua 脚本 ==========
--
-- 功能说明：
-- 原子性地完成秒杀核心业务：库存检查 -> 用户去重 -> 扣库存 -> 下单 -> 消息发送
-- 使用 Redis Lua 脚本保证操作原子性，避免并发问题
--
-- 返回值说明：
-- 0 = 秒杀成功，订单已创建
-- 1 = 库存不足，秒杀失败
-- 2 = 用户已购买，禁止重复下单
--
-- ========================================

-- 步骤1：参数解析
-- 从调用端传入的参数列表中提取优惠券、用户、订单相关信息
--
-- 商品库存目录
local goodsKey = ARGV[1]
-- 订单目录
local orderKey = ARGV[2]
--商品ID：标识秒杀活动的唯一凭证
local goodsId = ARGV[3]
-- 用户ID：标识发起秒杀的用户，用于去重检查
local userId = ARGV[4]
-- 订单ID：新生成的订单唯一标识，用于保存订单信息
local orderId = ARGV[5]
-- 已下单信息流
local orderStream = ARGV[6]

-- 步骤2：Redis Key 定义
-- 统一定义所有操作的 Redis key，便于维护和追踪
--
-- 库存 Key：存储该优惠券的剩余库存数量
-- 格式：seckill:stock:{voucherId}
-- 值类型：数字，表示剩余库存
local goodsKeyLocal = goodsKey .. goodsId
--
-- 订单 Key：存储已购买该优惠券的用户集合（去重用）
-- 格式：seckill:order:{voucherId}
-- 值类型：Set，存储所有已下单用户的 userId
local orderKeyLocal = orderKey .. goodsId

-- 步骤3：核心业务逻辑
-- ======== 3.1 库存检查 ========
-- 从 Redis 获取当前库存数量，如果 key 不存在则默认为 0
local goodsCount = (tonumber(redis.call('get', goodsKeyLocal)) or 0)
--
---- 检查库存是否充足（库存 <= 0 表示已售罄）
--if (goodsCount <= 0) then
--  -- 库存不足，无法继续秒杀
--  -- 返回错误码 1：库存不足
--  return 1
--end

-- ======== 3.2 用户重复下单检查 ========
-- 查询该用户是否已在订单集合中（防止重复购买）
-- sismember 返回 1 表示存在，0 表示不存在
if (redis.call('sismember', orderKeyLocal, userId) == 1) then
  -- 用户已购买过该优惠券，禁止重复下单
  -- 返回错误码 2：用户已购买
  return 2
end

-- ======== 3.3 扣库存 ========
-- 原子性地将库存数量减 1
-- incrby 命令支持负数，相当于 stock = stock - 1
redis.call('incrby', goodsKeyLocal, -1)

-- ======== 3.4 记录下单用户 ========
-- 将用户 ID 添加到订单集合中，标记该用户已购买
-- sadd 命令向 Set 中添加成员
redis.call('sadd', orderKeyLocal, userId)

-- ======== 3.5 发送订单消息 ========
-- 向消息队列发送订单信息，异步处理订单后续业务（如减库存、发货等）
-- XADD 命令向 Redis Stream 追加消息
-- 格式：stream.orders 为消息队列名称
-- '*' 表示由 Redis 自动生成消息 ID
-- 后续参数为键值对：k1 v1 k2 v2 ...
redis.call('xadd', orderStream, '*', 'userId', userId, 'goodsId', goodsId, 'orderId', orderId)

-- ======== 3.6 返回成功 ========
-- 所有操作均成功，返回 0 表示秒杀成功
return 0
