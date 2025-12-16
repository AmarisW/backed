package com.gqtian.redis.service;

import com.gqtian.redis.entity.RedisEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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
     * Redisson客户端
     */
    private final RedissonClient redissonClient;

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

        // 6. 批量操作
        redisTemplate.opsForValue().multiSet(new HashMap<>() {
            {
                put("user:gender", "男");
                put("user:city", "北京");
            }
        });

        List<Object> multiGet = redisTemplate.opsForValue()
            .multiGet(Arrays.asList("user:gender", "user:city"));
        log.info("批量获取结果：{}", multiGet);

        // 7. 获取所有user开头的key对应的value
        Set<String> userKeys = redisTemplate.keys("user:*");
        List<Object> userValues = redisTemplate.opsForValue().multiGet(userKeys);
        log.info("获取所有user开头的value：{}", userValues);
    }

    /**
     * Redisson分布式锁示例 - 通常使用的锁
     *
     * 演示如何使用Redisson实现基础的分布式锁，适用于大多数场景
     *
     * @param lockKey 锁的键名
     * @param waitTime 尝试获取锁的最大等待时间（秒）
     * @param leaseTime 获取锁后的过期时间（秒），防止程序崩溃导致死锁
     * @return 操作结果
     */
    public String lock(String lockKey, long waitTime, long leaseTime) {
        try {
            // 获取锁对象
            RLock lock = redissonClient.getLock(lockKey);

            // 尝试获取锁
            // waitTime: 尝试获取锁的最大等待时间
            // leaseTime: 获取锁后的过期时间，防止业务执行超时导致锁被其他线程释放
            // TimeUnit.SECONDS: 时间单位
            boolean isLocked = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);

            if (isLocked) {
                try {
                    // 获取锁成功，执行业务逻辑
                    log.info("成功获取分布式锁: {}", lockKey);

                    // 模拟业务处理（这里可以替换为实际业务代码）
                    Thread.sleep(2000);

                    log.info("业务处理完成");
                    return "分布式锁获取成功，业务处理完成";
                } finally {
                    // 确保释放锁
                    lock.unlock();
                    log.info("分布式锁已释放: {}", lockKey);
                }
            } else {
                log.warn("在指定时间内未能获取分布式锁: {}", lockKey);
                return "分布式锁获取失败，请稍后重试";
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取分布式锁被中断", e);
            return "获取分布式锁被中断";
        } catch (Exception e) {
            log.error("获取分布式锁异常", e);
            return "获取分布式锁异常: " + e.getMessage();
        }
    }

    /**
     * Redisson公平锁示例
     *
     * 公平锁确保多个线程按照获取锁的顺序来执行，避免某些线程长期无法获取锁
     * 但公平锁的性能低于普通锁，应在需要公平性的场景中使用
     *
     * @param lockKey 锁的键名
     * @return 操作结果
     */
    public String fairLock(String lockKey) {
        try {
            // 获取公平锁对象
            RLock fairLock = redissonClient.getFairLock(lockKey);

            log.info("尝试获取公平锁: {}", lockKey);
            // 获取公平锁（如果获取不到会一直阻塞，直到获取到为止）
            fairLock.lock();

            try {
                log.info("成功获取公平锁: {}", lockKey);
                // 执行业务逻辑
                Thread.sleep(2000);
                return "公平锁获取成功，按顺序执行";
            } finally {
                fairLock.unlock();
                log.info("公平锁已释放: {}", lockKey);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取公平锁被中断", e);
            return "获取公平锁被中断";
        } catch (Exception e) {
            log.error("获取公平锁异常", e);
            return "获取公平锁异常: " + e.getMessage();
        }
    }

    /**
     * Redisson读写锁示例
     *
     * 读写锁允许多个读线程同时执行，但写线程需要独占。
     * 适用于读多写少的场景
     *
     * @param lockKey 锁的键名
     * @param isWrite 是否执行写操作
     * @return 操作结果
     */
    public String readWriteLock(String lockKey, boolean isWrite) {
        try {
            // 获取读写锁对象
            RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(lockKey);

            if (isWrite) {
                // 获取写锁（需要独占，阻塞其他读和写操作）
                RLock writeLock = readWriteLock.writeLock();
                log.info("尝试获取写锁: {}", lockKey);
                writeLock.lock();

                try {
                    log.info("成功获取写锁，执行写操作");
                    Thread.sleep(2000);
                    return "写锁获取成功，执行写操作完成";
                } finally {
                    writeLock.unlock();
                    log.info("写锁已释放: {}", lockKey);
                }
            } else {
                // 获取读锁（多个读操作可以并发执行）
                RLock readLock = readWriteLock.readLock();
                log.info("尝试获取读锁: {}", lockKey);
                readLock.lock();

                try {
                    log.info("成功获取读锁，执行读操作");
                    Thread.sleep(1000);
                    return "读锁获取成功，执行读操作完成（支持并发读）";
                } finally {
                    readLock.unlock();
                    log.info("读锁已释放: {}", lockKey);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取读写锁被中断", e);
            return "获取读写锁被中断";
        } catch (Exception e) {
            log.error("获取读写锁异常", e);
            return "获取读写锁异常: " + e.getMessage();
        }
    }

    /**
     * Redisson信号量示例
     *
     * 信号量用于限制同时访问某个资源的线程数量
     * 适用于资源限流、连接池管理等场景
     *
     * @param semaphoreKey 信号量的键名
     * @param permits 允许的并发数量
     * @return 操作结果
     */
    public String semaphore(String semaphoreKey, int permits) {
        try {
            // 获取信号量对象
            RSemaphore semaphore = redissonClient.getSemaphore(semaphoreKey);

            // 初始化信号量（只在第一次需要）
            if (semaphore.availablePermits() == 0) {
                semaphore.trySetPermits(permits);
            }

            log.info("尝试获取信号量，可用许可数: {}", semaphore.availablePermits());
            // 获取一个信号量许可（如果没有可用许可会阻塞）
            semaphore.acquire();

            try {
                log.info("成功获取信号量许可");
                // 执行业务逻辑
                Thread.sleep(2000);
                log.info("业务处理完成，剩余许可数: {}", semaphore.availablePermits());
                return "信号量获取成功，当前许可数: " + semaphore.availablePermits();
            } finally {
                semaphore.release();
                log.info("信号量许可已释放，剩余许可数: {}", semaphore.availablePermits());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取信号量被中断", e);
            return "获取信号量被中断";
        } catch (Exception e) {
            log.error("获取信号量异常", e);
            return "获取信号量异常: " + e.getMessage();
        }
    }
}
