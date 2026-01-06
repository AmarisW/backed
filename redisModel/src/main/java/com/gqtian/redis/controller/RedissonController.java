
package com.gqtian.redis.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gqtian.redis.service.RedissonService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Redisson控制器
 *
 * 提供Redis各种操作的API端点，包括基本的字符串操作和分布式锁示例
 *
 * @author gqtian
 * @date 2025/12/15 17:28
 */
@Slf4j
@RestController
@RequestMapping("/redisson")
@RequiredArgsConstructor
public class RedissonController {

    private final RedissonService redissonService;

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
    @GetMapping("/lock")
    public String lock(@RequestParam(defaultValue = "distributed_lock") String lockKey,
        @RequestParam(defaultValue = "10") long waitTime, @RequestParam(defaultValue = "30") long leaseTime) {
        return redissonService.lock(lockKey, waitTime, leaseTime);
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
    @GetMapping("/fair-lock")
    public String fairLock(@RequestParam(defaultValue = "fair_distributed_lock") String lockKey) {
        return redissonService.fairLock(lockKey);
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
    @GetMapping("/read-write-lock")
    public String readWriteLock(@RequestParam(defaultValue = "read_write_lock") String lockKey,
        @RequestParam(defaultValue = "false") boolean isWrite) {
        return redissonService.readWriteLock(lockKey, isWrite);
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
    @GetMapping("/semaphore")
    public String semaphore(@RequestParam(defaultValue = "semaphore_lock") String semaphoreKey,
        @RequestParam(defaultValue = "3") int permits) {
        return redissonService.semaphore(semaphoreKey, permits);
    }

}