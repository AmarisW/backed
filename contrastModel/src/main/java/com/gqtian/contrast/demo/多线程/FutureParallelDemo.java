package com.gqtian.contrast.demo.多线程;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 🤔 线程串行化
 * 
 * 演示CompletableFuture的串行化执行方式：
 * 1. thenRunAsync() - 无输入无输出的串行任务
 * 2. thenAccept() - 有输入无输出的串行任务
 * 3. thenApplyAsync() - 有输入有输出的串行任务
 *
 * @author gqtian
 * @date 2023/10/20 15:07
 */
@Service
public class FutureParallelDemo {

    @Resource
    @Qualifier("queryThread")
    private ThreadPoolTaskExecutor threadPool;

    /**
     * 3.线程串行化（把后边的线程和前边的串起来）
     * 
     * 演示三种不同的串行化执行方式及其特点：
     * 1. thenRunAsync - 不接收前一个任务的结果，也不产生新的结果
     * 2. thenAccept - 接收前一个任务的结果，但不产生新的结果
     * 3. thenApplyAsync - 接收前一个任务的结果，并产生新的结果
     */
    public void parent() {
        // thenRunAsync()无返回值
        // 适用于前一个任务完成后需要执行一些不依赖其结果的操作
        CompletableFuture<Void> voidCompletableFuture1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("线程号为***" + Thread.currentThread().getId());
            int i = 5;
            System.out.println("---------" + i);
            return i;
        }, threadPool).thenRunAsync(() -> {
            System.out.println("thenRunAsync，不可接受传来的值，自己无返回值的串行化---");
        }, threadPool);

        try {
            System.out.println("thenRunAsync " + voidCompletableFuture1.get());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // thenAccept(x)
        // 适用于需要使用前一个任务的结果但不需要返回新结果的场景
        CompletableFuture<Void> voidCompletableFuture2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("线程号为***" + Thread.currentThread().getId());
            int i = 5;
            System.out.println("---------" + i);
            return i;
        }, threadPool).thenAccept((r) -> {
            System.out.println("thenAccept可接受传来的值，自己无返回值的串行化---" + r);
        });

        try {
            System.out.println("thenAccept " + voidCompletableFuture2.get());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // thenApply(x)
        // 适用于需要基于前一个任务的结果计算并返回新结果的场景
        CompletableFuture<Integer> voidCompletableFuture3 = CompletableFuture.supplyAsync(() -> {
            System.out.println("线程号为***" + Thread.currentThread().getId());
            int i = 5;
            System.out.println("---------" + i);
            return i;
        }, threadPool).thenApplyAsync((r) -> {
            System.out.println("thenApply可接受传来的值，自己有返回值的串行化---");
            return 10 + r;
        }, threadPool);

        try {
            System.out.println("thenApply " + voidCompletableFuture3.get());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}