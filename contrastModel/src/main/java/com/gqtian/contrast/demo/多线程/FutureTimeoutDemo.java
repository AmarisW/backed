package com.gqtian.contrast.demo.多线程;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 🤨 关于任务超时的设置
 * @className PracticalTimeoutDemo
 * @author gqtian
 * @date 2025/12/5 10:04
 */
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
        System.out.println("=== 示例 (completeOnTimeout) ===");
        CompletableFuture<String> fastService = CompletableFuture.supplyAsync(() -> callExternalService(1), executor)
            .completeOnTimeout("快速服务超时 - 使用缓存", 1, TimeUnit.SECONDS);

        CompletableFuture<String> slowService = CompletableFuture.supplyAsync(() -> callExternalService(2), executor)
            .completeOnTimeout("慢速服务超时 - 使用默认值", 1, TimeUnit.SECONDS);

        System.out.println("快速服务: " + fastService.get());
        System.out.println("慢速服务: " + slowService.get());

        // 场景2: 使用 orTimeout 实现快速失败
        System.out.println("\n=== 示例 (orTimeout) ===");

        CompletableFuture<String> whenCompleteService =
            CompletableFuture.supplyAsync(() -> callExternalService(2), executor) // 调用外部服务
                .orTimeout(1, TimeUnit.SECONDS) // 设置超时时间

                // whenComplete用法 : 不处理异常，继续传播 | 总是执行（成功/失败都执行）|
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        System.out.println(" whenComplete 服务超时");
                    }
                });

        try {
            System.out.println("whenComplete 服务结果: " + whenCompleteService.get());
        } catch (Exception e) {
            System.out.println("whenComplete 错误: " + e.getCause());
        }

        CompletableFuture<String> exceptionallyService =
            CompletableFuture.supplyAsync(() -> callExternalService(2), executor) // 调用外部服务
                .orTimeout(1, TimeUnit.SECONDS) // 设置超时时间
                // == exceptionally用法 == //
                .exceptionally(ex -> { // 异常处理
                    return "exceptionally 服务超时";
                });

        try {
            System.out.println(" exceptionallyService 服务结果: " + exceptionallyService.get());
        } catch (Exception e) {
            System.out.println(" exceptionallyService 错误: " + e.getCause());
        }

        // 场景3: 组合使用
        System.out.println("\n=== 组合使用示例 ===");
        CompletableFuture<String> combined = CompletableFuture.supplyAsync(() -> callExternalService(1), executor)
            .orTimeout(800, TimeUnit.MILLISECONDS).exceptionally(ex -> {
                System.out.println("主服务失败，尝试备份服务...");
                return null; // 触发 thenApply
            }).thenApply(result -> {
                if (result == null) {
                    // 调用备份服务
                    return CompletableFuture.supplyAsync(() -> callExternalService(3), executor)
                        .completeOnTimeout("备份服务超时", 500, TimeUnit.MILLISECONDS).join();
                }
                return result;
            });

        System.out.println("组合结果: " + combined.get());

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
    }
}
