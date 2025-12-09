package com.gqtian.contrast.demo.多线程;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 🤔 线程池监控
 * 
 * 演示如何通过继承ThreadPoolExecutor并重写其钩子方法来监控线程池的运行状态
 * ThreadPoolExecutor提供了三个可重写的钩子方法用于监控:
 * 1. beforeExecute(Thread t, Runnable r) - 任务执行前调用
 * 2. afterExecute(Runnable r, Throwable t) - 任务执行后调用
 * 3. terminated() - 线程池终止时调用
 *
 * @author gqtian
 * @date 2023/08/25 16:36
 */
public class 线程池监控 {

    public static void main(String[] args) {
        /*
         * 创建自定义ThreadPoolExecutor以实现线程池监控
         * 参数说明:
         *   - corePoolSize: 核心线程数 1
         *   - maximumPoolSize: 最大线程数 1
         *   - keepAliveTime: 空闲线程存活时间 1秒
         *   - unit: 存活时间单位 秒
         *   - workQueue: 工作队列，容量为1的有界队列
         */
        ExecutorService executor = new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1)) {
            /**
             * 任务执行前的钩子方法
             * 
             * @param t 执行任务的线程
             * @param r 将要执行的任务
             */
            @Override
            protected void beforeExecute(Thread t, Runnable r) {
                System.out.println("beforeExecute is called - 任务即将执行");
                // 可在此处添加任务执行前的监控逻辑，如记录开始时间、任务信息等
            }

            /**
             * 任务执行后的钩子方法
             * 
             * @param r 已执行的任务
             * @param t 任务执行过程中抛出的异常（如果没有异常则为null）
             */
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                System.out.println("afterExecute is called - 任务执行完成");
                // 可在此处添加任务执行后的监控逻辑，如记录执行时间、处理结果、异常信息等
                if (t != null) {
                    System.err.println("任务执行出现异常: " + t.getMessage());
                }
            }

            /**
             * 线程池终止时的钩子方法
             * 当线程池完全终止（所有任务都已完成并且没有新的任务提交）时调用
             */
            @Override
            protected void terminated() {
                System.out.println("terminated is called - 线程池已终止");
                // 可在此处添加线程池终止后的清理工作，如释放资源、发送通知等
            }
        };

        // 提交一个简单任务到线程池
        executor.submit(() -> {
            System.out.println("this is a task - 正在执行任务");
            // 模拟任务执行过程
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // 关闭线程池，不再接受新任务
        // shutdown()会等待已提交的任务执行完成后再终止线程池
        executor.shutdown();
    }
}
