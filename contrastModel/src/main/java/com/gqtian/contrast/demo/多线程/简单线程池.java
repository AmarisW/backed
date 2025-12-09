package com.gqtian.contrast.demo.多线程;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

/**
 * 🤔 简单的例子
 *
 * @author gqtian
 * @date 2023/08/25 16:22
 */
@Slf4j
public class 简单线程池 {

    public static void main(String[] args) throws InterruptedException {
        /*
         * Executors工厂类提供的常用线程池类型:
         * 
         * 1. newSingleThreadExecutor()        - 创建单一线程的线程池，所有任务按顺序在一个线程中执行
         * 2. newFixedThreadPool(int n)        - 创建固定数量的线程池，适合负载较重的服务器
         * 3. newCachedThreadPool()            - 创建可缓存的线程池，线程数量动态调整，适合执行大量短期异步任务
         * 4. newScheduledThreadPool(int n)    - 创建定时调度的线程池，支持定时及周期性任务执行
         * 5. newWorkStealingPool()            - 创建工作窃取线程池(Fork-Join)，适合执行耗时不均匀的并行任务
         */

        // 示例1: 单一线程池 - 所有任务串行执行
        example1SingleThreadExecutor();

        // 示例2: 固定大小线程池 - 固定数量线程并发执行
        example2FixedThreadPool();

        // 示例3: 缓存线程池 - 线程数量动态调整
        example3CachedThreadPool();

        // 示例4: 定时调度线程池 - 支持延迟和周期性任务
        example4ScheduledThreadPool();

        // 示例5: 工作窃取线程池 - 并行处理任务
        example5WorkStealingPool();
    }

    /**
     * 示例1: newSingleThreadExecutor - 单一线程池
     * 特点: 只有一个线程，所有任务按提交顺序依次执行，保证任务的顺序性
     */
    private static void example1SingleThreadExecutor() throws InterruptedException {
        log.info("========== 示例1: SingleThreadExecutor ==========");
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // 提交5个任务，它们将按顺序在同一个线程中执行
        for (int i = 1; i <= 5; i++) {
            int taskId = i;
            executor.submit(() -> {
                log.info("SingleThreadExecuto 任务{} 开始执行，线程:{}", taskId, Thread.currentThread().getName());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.info("SingleThreadExecutor 任务{} 执行完成", taskId);
            });
        }

        executor.shutdown();
        Thread.sleep(3000); // 等待任务执行完成
    }

    /**
     * 示例2: newFixedThreadPool - 固定大小线程池
     * 特点: 线程数量固定，适合负载稳定的场景
     */
    private static void example2FixedThreadPool() throws InterruptedException {
        log.info("========== 示例2: FixedThreadPool ==========");
        // 创建一个核心线程数为3的固定大小线程池
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // 提交10个任务，由3个线程并发执行
        for (int i = 1; i <= 10; i++) {
            int taskId = i;
            executor.submit(() -> {
                log.info("FixedThreadPool 任务{} 开始执行，线程:{}", taskId, Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.info("FixedThreadPool 任务{} 执行完成", taskId);
            });
        }

        executor.shutdown();
        Thread.sleep(5000); // 等待任务执行完成
    }

    /**
     * 示例3: newCachedThreadPool - 缓存线程池
     * 特点: 线程数量不固定，空闲线程60秒后回收，适合执行大量短期异步任务
     */
    private static void example3CachedThreadPool() throws InterruptedException {
        log.info("========== 示例3: CachedThreadPool ==========");
        ExecutorService executor = Executors.newCachedThreadPool();

        // 提交15个短期任务，线程池会根据需要创建新线程
        for (int i = 1; i <= 15; i++) {
            int taskId = i;
            executor.submit(() -> {
                log.info("CachedThreadPool 任务{} 开始执行，线程:{}", taskId, Thread.currentThread().getName());
                try {
                    Thread.sleep(200); // 短期任务
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.info("CachedThreadPool 任务{} 执行完成", taskId);
            });
            Thread.sleep(50); // 间隔提交
        }

        executor.shutdown();
        Thread.sleep(2000); // 等待任务执行完成
    }

    /**
     * 示例4: newScheduledThreadPool - 定时调度线程池
     * 特点: 支持延迟执行和周期性执行任务
     */
    private static void example4ScheduledThreadPool() throws InterruptedException {
        log.info("========== 示例4: ScheduledThreadPool ==========");
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

        /*
         * scheduleAtFixedRate: 按固定频率执行任务
         * 参数说明:
         *   - command: 要执行的任务
         *   - initialDelay: 初始延迟时间，这里为0表示立即开始
         *   - period: 任务执行的周期间隔，这里为2秒
         *   - unit: 时间单位
         *
         * 执行特点:
         *   - 以固定的频率执行，下次执行时间 = 上次执行开始时间 + period
         *   - 如果任务执行时间超过period，则任务执行完后立即开始下一次执行
         *   - 本例中任务耗时10秒，远超2秒的period，所以实际每10秒执行一次
         */
        executor.scheduleAtFixedRate(() -> {
            try {
                // 模拟任务执行耗时10秒
                Thread.sleep(10000);
                // 打印当前时间戳(秒)
                log.info("ScheduledThreadPool 固定频率任务 执行时间 {}", System.currentTimeMillis() / 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 0, 2, TimeUnit.SECONDS);

        /*
         * scheduleWithFixedDelay: 按固定延迟执行任务
         * 参数说明:
         *   - command: 要执行的任务
         *   - initialDelay: 初始延迟时间，这里为0表示立即开始
         *   - delay: 上一次任务结束到下一次任务开始的延迟时间
         *   - unit: 时间单位
         *
         * 执行特点:
         *   - 以固定的延迟执行，下次执行时间 = 上次执行结束时间 + delay
         *   - 本例中任务耗时5秒，加上2秒的delay，所以实际每7秒执行一次
         */
        executor.scheduleWithFixedDelay(() -> {
            try {
                // 模拟任务执行耗时5秒
                Thread.sleep(5000);
                // 打印当前时间戳(秒)
                log.info("ScheduledThreadPool 固定延迟任务 执行时间 {}", System.currentTimeMillis() / 1000);
                System.out.println(System.currentTimeMillis() / 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 0, 2, TimeUnit.SECONDS);

        /*
         * schedule: 延迟执行一次性任务
         * 参数说明:
         *   - command: 要执行的任务
         *   - delay: 延迟时间，这里为5秒
         *   - unit: 时间单位
         *
         * 执行特点:
         *   - 只执行一次，不会重复执行
         *   - 延迟指定时间后执行
         */
        executor.schedule(() -> log.info("ScheduledThreadPool 开始在5秒 执行时间 {}", System.currentTimeMillis() / 1000), 5,
            TimeUnit.SECONDS);

        Thread.sleep(7000); // 让周期性任务执行几次
        executor.shutdown();
    }

    /**
     * 示例5: newWorkStealingPool - 工作窃取线程池
     * 特点: 基于ForkJoinPool，适合处理耗时不均匀的并行任务
     */
    private static void example5WorkStealingPool() throws InterruptedException {
        log.info("========== 示例5: WorkStealingPool ==========");
        // 默认并行度为CPU核心数
        ExecutorService executor = Executors.newWorkStealingPool();

        // 提交耗时不同的任务，演示工作窃取机制
        for (int i = 1; i <= 8; i++) {
            int taskId = i;
            int sleepTime = (i % 3 + 1) * 500; // 不同任务耗时不同
            executor.submit(() -> {
                log.info("WorkStealingPool 任务{} 开始执行(耗时{}ms)，线程:{}", taskId, sleepTime,
                    Thread.currentThread().getName());
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.info("WorkStealingPool 任务{} 执行完成", taskId);
            });
        }

        executor.shutdown();
        Thread.sleep(5000); // 等待任务执行完成
    }
}
