package com.gqtian.fileStorage.demo.multithreadDemo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 🤔 简单的例子
 *
 * @author gqtian
 * @date 2023/08/25 16:22
 */
public class 简单线程池例子 {
    public static void main(String[] args) {
        /**
         * 创建单一线程的线程池 public static ExecutorService newSingleThreadExecutor(); 创建固定数量的线程池 public static ExecutorService
         * newFixedThreadPool(int nThreads); 创建带缓存的线程池 public static ExecutorService newCachedThreadPool(); 创建定时调度的线程池
         * public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize); 创建流式（fork-join）线程池 public
         * static ExecutorService newWorkStealingPool();
         */
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        
        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                System.out.println("线程名：" + Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
