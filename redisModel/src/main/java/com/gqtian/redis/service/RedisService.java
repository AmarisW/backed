package com.gqtian.redis.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import com.gqtian.redis.entity.RedisEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Redis相关服务
 * 
 * 提供Redis各种操作的业务逻辑，包括基础字符串操作和分布式锁实现
 * 
 * @className RedisService
 * @author gqtian
 * @date 2025/12/10 11:30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    /**
     * Redis模板
     */
    private final RedisTemplate<String, Object> redisTemplate;


    /**
     * 基础字符串操作
     *
     * 演示Redis的String类型操作，包括：
     * - 基本的键值对存取
     * - 自增操作
     * - 对象序列化存储
     * - 批量操作
     */
    public void stringOps() {
        // 1.1 存入键值对（无过期时间）
        redisTemplate.opsForValue().set("user:name", "张三");

        // 1.2 存入键值对（设置 10秒 过期）
        redisTemplate.opsForValue().set("user:age", 25, 10, TimeUnit.SECONDS);

        // 1.3 获取值
        String name = (String) redisTemplate.opsForValue().get("user:name");
        Integer age = (Integer) redisTemplate.opsForValue().get("user:age");
        log.info("用户名：{}，年龄：{}", name, age);

        // 4.1 自增（计数器场景） // 阅读量 +1
        String articleViewKey = "article:view";
        // 获取初始值 如果不存在则 初始化为 1
        redisTemplate.opsForValue().setIfAbsent(articleViewKey, 0);
        log.info("初始化阅读量：{}", redisTemplate.opsForValue().get(articleViewKey));
        // 4.2 批量自增
        for (int i = 0; i < 10; i++) {
            redisTemplate.opsForValue().increment(articleViewKey, 1);
        }
        // 4.3 获取结果
        Integer viewCount = (Integer) redisTemplate.opsForValue().get(articleViewKey);
        log.info("自增之后的阅读量：{}", viewCount);

        // 5.1 存放对象
        RedisEntity redisEntity = RedisEntity.builder().id(1L).name("张三").age(25).score(90.0)
            .price(89.9f).active(true).gender('男').status((byte) 1).level((short) 1)
            .createTime(LocalDateTime.now()).birthDate(LocalDate.now()).loginTime(LocalTime.now())
            .hobbies(Arrays.asList("reading", "swimming")).build();

        redisTemplate.opsForValue().set("user", redisEntity);
        // 5.2 取出对象值
        RedisEntity user = (RedisEntity) redisTemplate.opsForValue().get("user");
        log.info("取出对象值：{}", user);

        // 6.1 批量操作
        redisTemplate.opsForValue().multiSet(new HashMap<>() {
            {
                put("user:gender", "男");
                put("user:city", "北京");
            }
        });

        List<Object> multiGet = redisTemplate.opsForValue()
            .multiGet(Arrays.asList("user:gender", "user:city"));
        log.info("批量获取结果：{}", multiGet);

        // 7.1 获取所有user开头的key对应的value
        Set<String> userKeys = redisTemplate.keys("user:*");
        List<Object> userValues = redisTemplate.opsForValue().multiGet(userKeys);
        log.info("获取所有user开头的value：{}", userValues);
    }

    /**
     * 哈希操作
     *
     * 演示Redis的Hash类型操作，包括：
     * - 单个属性的存取
     * - 批量属性操作
     * - 属性删除
     * - 整个哈希数据的删除
     */
    public void hashOps() {

        // 1. 存入哈希数据（用户 ID 为 1001 的信息）
        HashOperations<String, Object, Object> hashOps = redisTemplate.opsForHash();
        hashOps.put("user:1001", "name", "李四");
        hashOps.put("user:1001", "age", 30);
        hashOps.put("user:1001", "email", "lisi@xxx.com");

        // 2. 获取单个属性
        String email = (String)hashOps.get("user:1001", "email");
        log.info("用户 1001 的邮箱：{}", email);

        // 3. 获取所有属性
        Map<Object, Object> userMap = hashOps.entries("user:1001");
        log.info("用户 1001 的属性：{}", userMap);

        // 4. 批量存入属性
        Map<Object, Object> newAttrs = new HashMap<>();
        newAttrs.put("gender", "男");
        newAttrs.put("city", "上海");
        hashOps.putAll("user:1001", newAttrs);

        // 5. 删除某个属性
        hashOps.delete("user:1001", "email");

        // 6. 删除整个哈希数据
        hashOps.delete("user:1001");
    }

    /**
     * 列表操作
     *
     * 演示Redis的List类型操作，包括：
     * - 队列操作（先进先出）
     * - 栈操作（先进后出）
     * - 列表范围查询
     * - 列表长度修剪
     */
    public void listOps() {

        ListOperations<String, Object> listOps = redisTemplate.opsForList();
        String key = "message:queue";

        // 1. 左进（队列：先进先出）
        listOps.leftPush(key, "消息1");
        listOps.leftPush(key, "消息2");
        listOps.leftPush(key, "消息3");

        // 2. 右出（获取并移除队尾元素）
        Object msg1 = listOps.rightPop(key);
        Object msg2 = listOps.rightPop(key);
        log.info("消费消息：{}，{}", msg1, msg2);

        // 3. 获取列表范围（0 到 -1 表示所有元素）
        List<Object> allMsg = listOps.range(key, 0, -1);
        log.info("剩余消息：{}", allMsg);

        // 4. 栈操作（左进左出）
        listOps.leftPush("stack", "元素A");
        listOps.leftPush("stack", "元素B");
        Object stackMsg1 = listOps.leftPop("stack");
        log.info("栈弹出元素：{}", stackMsg1);

        // 5. 设置列表长度
        listOps.trim(key, 0, 0); // 只保留第一个元素
    }

    /**
     * 集合操作
     *
     * 演示Redis的Set类型操作，包括：
     * - 元素的添加和移除
     * - 集合交集运算
     * - 集合并集运算
     * - 集合差集运算
     */
    public void setOps() {
        SetOperations<String, Object> setOps = redisTemplate.opsForSet();

        // 1. 向集合添加元素
        setOps.add("user:tags:1001", "Java", "Redis", "Spring Boot");
        setOps.add("user:tags:1002", "Java", "MySQL", "Docker");

        // 2. 获取集合所有元素
        Set<Object> tags1001 = setOps.members("user:tags:1001");
        log.info("用户 1001 标签：{}", tags1001);

        // 3. 交集（共同标签）
        Set<Object> commonTags = setOps.intersect("user:tags:1001", "user:tags:1002");
        log.info("共同标签：{}", commonTags);

        // 4. 并集（所有标签）
        Set<Object> allTags = setOps.union("user:tags:1001", "user:tags:1002");
        log.info("所有标签：{}", allTags);

        // 5. 差集（用户 1001 独有的标签）
        Set<Object> diffTags = setOps.difference("user:tags:1001", "user:tags:1002");
        log.info("用户 1001 独有标签：{}", diffTags);

        // 6. 移除元素
        setOps.remove("user:tags:1001", "Redis");
    }

    /**
     * 有序集合操作
     *
     * 演示Redis的ZSet类型操作，包括：
     * - 元素的添加和分数管理
     * - 升序和降序排列
     * - 分数增加
     * - 元素排名查询
     */
    public void zSetOps() {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        String key = "article:rank";

        // 1. 添加元素（分数为文章阅读量）
        zSetOps.add(key, "文章1001", 100);
        zSetOps.add(key, "文章1002", 200);
        zSetOps.add(key, "文章1003", 150);

        // 2. 按分数升序排列（0 到 -1 表示所有元素）
        Set<Object> ascRank = zSetOps.range(key, 0, -1);
        log.info("阅读量升序排行：{}", ascRank);

        // 3. 按分数降序排列（排行榜常用）
        Set<Object> descRank = zSetOps.reverseRange(key, 0, -1);
        log.info("阅读量降序排行：{}", descRank);

        // 4. 增加元素分数（阅读量 +50）
        zSetOps.incrementScore(key, "文章1001", 50);

        // 5. 获取元素分数
        Double score = zSetOps.score(key, "文章1001");
        log.info("文章1001 最新阅读量：{}", score);

        // 6. 获取元素排名（降序，从 0 开始）
        Long rank = zSetOps.reverseRank(key, "文章1001");
        log.info("文章1001 排名：第{}名", (rank + 1));
    }

}
