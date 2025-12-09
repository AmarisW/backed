package com.gqtian.contrast.demo.多线程;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

/**
 * 🤨 关于任务超时的设置
 * 
 * 演示CompletableFuture的超时处理机制：
 * 1. completeOnTimeout - 超时时提供默认值，任务继续执行
 * 2. orTimeout - 超时时抛出异常，任务被取消
 * 3. 组合使用超时处理机制实现优雅降级和快速失败
 * 
 * @className PracticalTimeoutDemo
 * @author gqtian
 * @date 2025/12/5 10:04
 */
@Slf4j
public class FutureTimeoutDemo {

    /**
     * 模拟外部服务调用
     * @param serviceId 服务ID
     * @return 服务响应
     */
    static String callExternalService(int serviceId) {
        try {
            // 模拟不同的响应时间
            int delay = serviceId == 1 ? 500 : 3000;
            Thread.sleep(delay);
            return "服务 " + serviceId + " 的响应";
        } catch (InterruptedException e) {
            return "服务 " + serviceId + " 被中断";
        }
    }

    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newCachedThreadPool();

        // 场景1: 使用 completeOnTimeout 实现优雅降级
        // 适用于希望在超时时提供默认值而不是直接失败的场景
        log.info("=== 示例 (completeOnTimeout) ===");
        CompletableFuture<String> fastService = CompletableFuture.supplyAsync(() -> callExternalService(1), executor)
            .completeOnTimeout("快速服务超时 - 使用缓存", 1, TimeUnit.SECONDS);

        CompletableFuture<String> slowService = CompletableFuture.supplyAsync(() -> callExternalService(2), executor)
            .completeOnTimeout("慢速服务超时 - 使用默认值", 1, TimeUnit.SECONDS);

        log.info("快速服务: {}", fastService.get());
        log.info("慢速服务: {}", slowService.get());

        // 场景2: 使用 orTimeout 实现快速失败
        // 适用于希望在超时时立即失败而不是等待的场景
        log.info("\n=== 示例 (orTimeout) ===");

        CompletableFuture<String> whenCompleteService =
            CompletableFuture.supplyAsync(() -> callExternalService(2), executor) // 调用外部服务
                .orTimeout(1, TimeUnit.SECONDS) // 设置超时时间

                // whenComplete用法 : 不处理异常，继续传播 | 总是执行（成功/失败都执行）|
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.info(" whenComplete 服务超时");
                    }
                });

        try {
            log.info("whenComplete 服务结果: {}", whenCompleteService.get());
        } catch (Exception e) {
            log.info("whenComplete 错误: {}", e.getCause());
        }

        CompletableFuture<String> exceptionallyService =
            CompletableFuture.supplyAsync(() -> callExternalService(2), executor) // 调用外部服务
                .orTimeout(1, TimeUnit.SECONDS) // 设置超时时间
                // exceptionally用法
                .exceptionally(ex -> { // 异常处理
                    return "exceptionally 服务超时";
                });

        try {
            log.info(" exceptionallyService 服务结果: {}", exceptionallyService.get());
        } catch (Exception e) {
            log.info(" exceptionallyService 错误: {}", e.getCause());
        }

        // 场景3: 组合使用
        // 演示如何结合超时处理机制实现复杂的容错逻辑
        log.info("\n=== 组合使用示例 ===");
        CompletableFuture<String> combined = CompletableFuture.supplyAsync(() -> callExternalService(1), executor)
            .orTimeout(800, TimeUnit.MILLISECONDS).exceptionally(ex -> {
                log.info("主服务失败，尝试备份服务...");
                return null; // 触发 thenApply
            }).thenApply(result -> {
                if (result == null) {
                    // 调用备份服务
                    return CompletableFuture.supplyAsync(() -> callExternalService(3), executor)
                        .completeOnTimeout("备份服务超时", 500, TimeUnit.MILLISECONDS).join();
                }
                return result;
            });

        log.info("组合结果: {}", combined.get());

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
    }
}