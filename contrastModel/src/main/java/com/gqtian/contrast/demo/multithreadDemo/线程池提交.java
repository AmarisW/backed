package com.gqtian.contrast.demo.multithreadDemo;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 🤔 线程池提交任务
 *
 * @author gqtian
 * @date 2023/08/25 16:32
 */
public class 线程池提交 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(() -> System.out.println("hello"));

        Future<Long> future = executor.submit(() -> {
            System.out.println("task is executed");
            return System.currentTimeMillis();
        });
        System.out.println("task execute time is: " + future.get());
    }
}
