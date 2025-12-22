package com.gqtian.redis.service;

import java.util.Collections;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import com.gqtian.redis.constant.SystemConstant.REDIS_LUA_SCRIPT_KEY;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 🤨 Lua脚本调用
 * @className LuaService
 * @author gqtian
 * @date 2025/12/19 16:12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LuaService {

    /**
     * StringRedisTemplate：使用字符串序列化器，确保脚本参数以字符串形式传递给 Lua
     * （相比 RedisTemplate，StringRedisTemplate 避免了 JSON 序列化问题）
     */
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * Redis Lua 脚本对象
     * 
     * 该脚本用于实现秒杀业务的原子性操作。通过在 Redis 服务端执行 Lua 脚本，
     * 确保库存检查、去重、扣库存、记录订单等多个操作在原子性事务中完成，
     * 避免并发环境下的竞态条件和数据一致性问题。
     */
    public static final DefaultRedisScript<Long> REDIS_LUA_SCRIPT;

    /**
     * 静态初始化块：加载并配置 Lua 脚本
     * 
     * 在类加载时执行以下初始化步骤：
     * 1. 创建 DefaultRedisScript 实例，用于执行 Lua 脚本
     * 2. 从 classpath 中加载 "lua/redisLua.lua" 脚本文件
     *    - 脚本路径相对于 src/main/resources 目录
     *    - 脚本文件包含秒杀业务的核心逻辑
     * 3. 设置脚本返回值类型为 Long.class
     *    - 对应 Lua 脚本的返回值：0(成功)、1(库存不足)、2(用户已购买)
     * 
     * 这样做的优势：
     * - 脚本只加载一次，避免重复读取文件造成的性能开销
     * - 在应用启动阶段发现脚本文件缺失的问题，及早报错
     * - 提高运行时的执行效率
     */
    static {
        REDIS_LUA_SCRIPT = new DefaultRedisScript<>();
        REDIS_LUA_SCRIPT.setLocation(new ClassPathResource("lua/redisLua.lua"));
        REDIS_LUA_SCRIPT.setResultType(Long.class);
    }

    public String runLuaScript(String goodsId, String orderId, String userId) {

        /**
         * 执行 Lua 脚本实现秒杀业务的原子性操作
         * 
         * REDIS_LUA_SCRIPT 的作用：
         * 通过 Redis Lua 脚本保证秒杀核心业务流程的原子性，避免并发竞态条件。
         * 
         * 执行流程（脚本内部顺序执行）：
         * 1. 库存检查：获取当前商品库存，库存 <= 0 则返回错误码 1（库存不足）
         * 2. 用户去重：检查用户是否已购买过该商品，已购买则返回错误码 2（禁止重复下单）
         * 3. 扣库存：原子性将库存数量减 1（使用 incrby 命令）
         * 4. 记录下单用户：将用户 ID 添加到订单集合中，标记该用户已购买
         * 5. 发送订单消息：向 Redis Stream 异步发送订单消息，用于后续业务处理（如减库存、发货等）
         * 6. 返回成功：所有操作原子完成后返回 0
         * 
         * 返回值说明：
         *   0 = 秒杀成功，订单已创建
         *   1 = 库存不足，秒杀失败
         *   2 = 用户已购买，禁止重复下单
         * 
         * Redis Key 构成（拼接规则）：
         *   库存 Key =  goodsKey + goodsId  (例如：lua:goods:1002)
         *   订单 Key = orderKey + goodsId  (例如：lua:order:1002)
         * 
         * 传入参数说明（按 ARGV 在 Lua 脚本中的顺序）：
         *   goodsKey：商品库存 key 前缀（"lua:goods:"）
         *   orderKey：订单记录 key 前缀（"lua:order:"）
         *   goodsId：商品 ID（唯一标识秒杀活动）
         *   orderId：订单 ID（新生成的订单唯一标识）
         *   userId：用户 ID（发起秒杀的用户，用于去重和消息记录）
         */
        Long result = stringRedisTemplate.execute(REDIS_LUA_SCRIPT, Collections.emptyList(),
            REDIS_LUA_SCRIPT_KEY.GOODS_KEY, REDIS_LUA_SCRIPT_KEY.ORDER_KEY, goodsId, orderId, userId,
            REDIS_LUA_SCRIPT_KEY.ORDER_STREAM);
        return result.toString();
    }

}
