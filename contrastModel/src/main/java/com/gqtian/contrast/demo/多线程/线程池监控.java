package com.gqtian.contrast.demo.多线程;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 🤔 线程池监控
 *
 * @author gqtian
 * @date 2023/08/25 16:36
 */
public class 线程池监控 {

    /**
     * 1. beforeExecute()：线程池中任务运行前执行 long getTaskCount()，获取已经执行或正在执行的任务数 long getCompletedTaskCount()，获取已经执行的任务数 int
     * getLargestPoolSize()，获取线程池曾经创建过的最大线程数，根据这个参数，我们可以知道线程池是否满过 int getPoolSize()，获取线程池线程数 int
     * getActiveCount()，获取活跃线程数（正在执行任务的线程数） 其次，ThreadPoolExecutor留给我们自行处理的方法有3个，它在ThreadPoolExecutor中为空实现（也就是什么都不做）。
     *
     * protected void beforeExecute(Thread t, Runnable r) // 任务执行前被调用 protected void afterExecute(Runnable r, Throwable
     * t) // 任务执行后被调用 protected void terminated() // 线程池结束后被调用
     */
    public static void main(String[] args) {
        ExecutorService executor = new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1)) {
            @Override
            protected void beforeExecute(Thread t, Runnable r) {
                System.out.println("beforeExecute is called");
            }

            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                System.out.println("afterExecute is called");
            }

            @Override
            protected void terminated() {
                System.out.println("terminated is called");
            }
        };

        executor.submit(() -> System.out.println("this is a task"));
        executor.shutdown();
    }
}
