package com.gqtian.contrast.demo.多线程;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import lombok.extern.slf4j.Slf4j;

/**
 * 虚拟线程使用案例（需要JDK 21+）
 * <p>
 * Java 21正式引入的虚拟线程(Virtual Thread)是一种轻量级线程，由JVM管理而非操作系统。
 * 虚拟线程的优势：
 * 1. 创建成本极低，可以创建数百万个虚拟线程
 * 2. 内存占用小，每个虚拟线程只占用几KB内存
 * 3. 适合处理大量IO密集型任务
 * 4. 简化并发编程，使用传统的同步阻塞方式即可获得异步性能
 *
 * @author gqtian
 * @date 2026/1/5
 */
@Slf4j
public class VirtualThreadDemo {

    public static void main(String[] args) throws InterruptedException {
        log.info("========== 虚拟线程演示开始 ==========");

        // 案例1：创建单个虚拟线程的三种方式
        demo1CreateSingleVirtualThread();

        // 案例2：使用虚拟线程执行器批量创建虚拟线程
        demo2VirtualThreadExecutor();

        // 案例3：虚拟线程处理大量并发任务
        demo3MassiveConcurrency();

        // 案例4：虚拟线程处理IO密集型任务
        demo4IoIntensiveTask();

        // 案例5：虚拟线程结合CompletableFuture异步编排
        demo5CompletableFutureWithVirtualThread();

        // 案例6：虚拟线程批量异步处理并聚合结果
        demo6BatchAsyncProcessing();

        // 案例7：虚拟线程实现复杂异步流程编排
        demo7ComplexAsyncOrchestration();

        log.info("========== 虚拟线程演示结束 ==========");
    }

    /**
     * 案例1：创建单个虚拟线程的三种方式
     */
    private static void demo1CreateSingleVirtualThread() throws InterruptedException {
        log.info("\n【案例1】创建单个虚拟线程的三种方式");

        // 方式1：使用Thread.ofVirtual().start()创建并启动
        Thread vThread1 = Thread.ofVirtual()
            .start(() -> log.info("方式1 - Thread.ofVirtual().start()，线程: {}", Thread.currentThread()));
        vThread1.join();

        // 方式2：使用Thread.startVirtualThread()创建并启动（最简洁）
        Thread vThread2 = Thread.startVirtualThread(
            () -> log.info("方式2 - Thread.startVirtualThread()，线程: {}", Thread.currentThread()));
        vThread2.join();

        // 方式3：使用Thread.ofVirtual().unstarted()创建未启动的虚拟线程
        Thread vThread3 = Thread.ofVirtual()
            .unstarted(() -> log.info("方式3 - Thread.ofVirtual().unstarted()，线程: {}", Thread.currentThread()));
        vThread3.start(); // 手动启动
        vThread3.join();

        // 方式4：使用Thread.ofVirtual().name()设置线程名称
        Thread vThread4 = Thread.ofVirtual().name("custom-virtual-thread")
            .start(() -> log.info("方式4 - 自定义线程名，线程: {}", Thread.currentThread()));
        vThread4.join();
    }

    /**
     * 案例2：使用虚拟线程执行器批量创建虚拟线程
     */
    private static void demo2VirtualThreadExecutor() {
        log.info("\n【案例2】使用虚拟线程执行器");

        // 创建虚拟线程执行器（每个任务一个新的虚拟线程）
        var factory = Thread.ofVirtual().name("virtual-", 0).factory();
        try (var executor = Executors.newThreadPerTaskExecutor(factory)) {
            // 提交10个任务
            IntStream.range(1, 11).forEach(i -> {
                executor.submit(() -> {
                    log.info("任务 {} 正在执行，线程: {}", i, Thread.currentThread());
                    try {
                        Thread.sleep(100); // 模拟IO操作
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    log.info("任务 {} 执行完成", i);
                });
            });
        } // try-with-resources会自动关闭executor并等待所有任务完成

        log.info("所有任务已完成");
    }

    /**
     * 案例3：虚拟线程处理大量并发任务
     * 虚拟线程可以轻松创建数万甚至数百万个线程
     */
    private static void demo3MassiveConcurrency() {
        log.info("\n【案例3】虚拟线程处理大量并发任务");

        int taskCount = 10000; // 创建1万个虚拟线程
        long startTime = System.currentTimeMillis();

        var factory = Thread.ofVirtual().name("virtual-", 0).factory();
        try (var executor = Executors.newThreadPerTaskExecutor(factory)) {
            IntStream.range(0, taskCount).forEach(i -> {
                executor.submit(() -> {
                    try {
                        Thread.sleep(10); // 模拟短时IO操作
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            });
        }

        long duration = System.currentTimeMillis() - startTime;
        log.info("{} 个虚拟线程任务执行耗时: {} ms", taskCount, duration);
    }

    /**
     * 案例4：虚拟线程处理IO密集型任务
     * 模拟HTTP请求、数据库查询等IO操作
     * 虚拟线程在遇到阻塞IO时会自动挂起，释放底层平台线程
     */
    private static void demo4IoIntensiveTask() {
        log.info("\n【案例4】虚拟线程处理IO密集型任务");

        var factory = Thread.ofVirtual().name("virtual-", 0).factory();
        try (var executor = Executors.newThreadPerTaskExecutor(factory)) {
            // 模拟100个并发的HTTP请求
            IntStream.range(1, 101).forEach(i -> {
                executor.submit(() -> simulateHttpRequest(i));
            });
        }

        log.info("所有HTTP请求处理完成");
    }

    /**
     * 模拟HTTP请求
     * 虚拟线程在遇到阻塞IO时会自动挂起，释放底层平台线程，使其可以执行其他虚拟线程
     *
     * @param requestId 请求ID
     */
    private static void simulateHttpRequest(int requestId) {
        try {
            log.info("请求 {} 开始处理，虚拟线程: {}", requestId, Thread.currentThread());

            // 模拟网络IO延迟（虚拟线程会在此处挂起）
            Thread.sleep(100);

            // 模拟处理响应
            String response = "Response for request " + requestId;
            log.info("请求 {} 完成，响应: {}", requestId, response);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("请求 {} 被中断", requestId, e);
        }
    }

    /**
     * 案例5：虚拟线程结合CompletableFuture异步编排
     * 演示如何使用虚拟线程作为CompletableFuture的执行器
     */
    private static void demo5CompletableFutureWithVirtualThread() {
        log.info("\n【案例5】虚拟线程结合CompletableFuture异步编排");

        // 创建虚拟线程执行器
        var factory = Thread.ofVirtual().name("virtual-async-", 0).factory();
        var executor = Executors.newThreadPerTaskExecutor(factory);

        try {
            // 场景：模拟用户下单流程 - 查询库存 -> 扣减库存 -> 创建订单
            CompletableFuture<String> orderProcess = CompletableFuture
                // 步骤1：查询库存
                .supplyAsync(() -> {
                    log.info("步骤1：查询库存，线程: {}", Thread.currentThread());
                    sleep(100);
                    return "库存充足";
                }, executor)
                // 步骤2：扣减库存
                .thenApplyAsync(stock -> {
                    log.info("步骤2：扣减库存 [{}]，线程: {}", stock, Thread.currentThread());
                    sleep(100);
                    return "库存已扣减";
                }, executor)
                // 步骤3：创建订单
                .thenApplyAsync(result -> {
                    log.info("步骤3：创建订单 [{}]，线程: {}", result, Thread.currentThread());
                    sleep(100);
                    return "订单创建成功：ORDER-12345";
                }, executor)
                // 步骤4：异常处理
                .exceptionally(ex -> {
                    log.error("订单处理失败", ex);
                    return "订单创建失败：" + ex.getMessage();
                });

            // 等待结果
            String orderResult = orderProcess.join();
            log.info("最终结果: {}", orderResult);

        } finally {
            executor.close();
        }
    }

    /**
     * 案例6：虚拟线程批量异步处理并聚合结果
     * 演示并行处理多个任务并等待所有结果完成
     */
    private static void demo6BatchAsyncProcessing() {
        log.info("\n【案例6】虚拟线程批量异步处理并聚合结果");

        var factory = Thread.ofVirtual().name("virtual-batch-", 0).factory();
        var executor = Executors.newThreadPerTaskExecutor(factory);

        try {
            // 模拟查询多个商品的价格信息
            List<Integer> productIds = List.of(1001, 1002, 1003, 1004, 1005);

            // 为每个商品ID创建异步查询任务
            List<CompletableFuture<String>> futures = productIds.stream()
                .map(productId -> CompletableFuture.supplyAsync(() -> {
                    log.info("查询商品 {} 的价格，线程: {}", productId, Thread.currentThread());
                    sleep(100); // 模拟数据库查询
                    double price = productId * 99.9;
                    return String.format("商品%d: ¥%.2f", productId, price);
                }, executor))
                .toList();

            // 等待所有查询完成并聚合结果
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
            );

            // 获取所有结果
            allFutures.thenRun(() -> {
                log.info("\n所有商品价格查询完成：");
                futures.forEach(future -> {
                    String result = future.join();
                    log.info("  - {}", result);
                });
            }).join();

        } finally {
            executor.close();
        }
    }

    /**
     * 案例7：虚拟线程实现复杂异步流程编排
     * 演示多个异步任务的组合、依赖关系和分支处理
     */
    private static void demo7ComplexAsyncOrchestration() {
        log.info("\n【案例7】虚拟线程实现复杂异步流程编排");

        var factory = Thread.ofVirtual().name("virtual-complex-", 0).factory();
        var executor = Executors.newThreadPerTaskExecutor(factory);

        try {
            // 场景：电商下单流程
            // 1. 并行执行：验证用户信息 & 验证商品库存
            // 2. 两者都成功后：计算价格
            // 3. 价格确认后：并行执行 创建订单 & 发送通知

            CompletableFuture<String> userValidation = CompletableFuture.supplyAsync(() -> {
                log.info("验证用户信息，线程: {}", Thread.currentThread());
                sleep(150);
                return "用户验证通过";
            }, executor);

            CompletableFuture<String> stockValidation = CompletableFuture.supplyAsync(() -> {
                log.info("验证商品库存，线程: {}", Thread.currentThread());
                sleep(100);
                return "库存验证通过";
            }, executor);

            // 使用thenCombine等待两个验证都完成
            CompletableFuture<String> priceCalculation = userValidation
                .thenCombine(stockValidation, (user, stock) -> {
                    log.info("计算订单价格 [用户: {}, 库存: {}]，线程: {}",
                        user, stock, Thread.currentThread());
                    sleep(100);
                    return "订单总价: ¥299.00";
                });

            // 价格确认后，并行执行订单创建和发送通知
            CompletableFuture<String> createOrder = priceCalculation.thenApplyAsync(price -> {
                log.info("创建订单 [{}]，线程: {}", price, Thread.currentThread());
                sleep(120);
                return "订单号: ORDER-2026010501";
            }, executor);

            CompletableFuture<String> sendNotification = priceCalculation.thenApplyAsync(price -> {
                log.info("发送通知 [{}]，线程: {}", price, Thread.currentThread());
                sleep(80);
                return "通知已发送";
            }, executor);

            // 等待订单创建和通知发送都完成
            CompletableFuture<String> finalResult = createOrder
                .thenCombine(sendNotification, (order, notification) -> {
                    log.info("完成订单流程，线程: {}", Thread.currentThread());
                    return String.format("订单处理完成 - %s, %s", order, notification);
                });

            // 获取最终结果
            String result = finalResult.join();
            log.info("\n最终结果: {}", result);

            // 演示anyOf - 任意一个完成即可
            log.info("\n演示anyOf：查询多个支付渠道，取最快响应的");
            CompletableFuture<String> wechatPay = CompletableFuture.supplyAsync(() -> {
                sleep(150);
                return "微信支付响应";
            }, executor);

            CompletableFuture<String> alipay = CompletableFuture.supplyAsync(() -> {
                sleep(100);
                return "支付宝响应";
            }, executor);

            CompletableFuture<String> unionPay = CompletableFuture.supplyAsync(() -> {
                sleep(120);
                return "银联支付响应";
            }, executor);

            CompletableFuture<Object> fastest = CompletableFuture.anyOf(wechatPay, alipay, unionPay);
            log.info("最快的支付渠道: {}", fastest.join());

        } finally {
            executor.close();
        }
    }

    /**
     * 工具方法：休眠指定毫秒数
     *
     * @param millis 毫秒数
     */
    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
