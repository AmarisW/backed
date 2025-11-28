package com.gqtian.contrast.demo.多线程;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 🤔 线程串行化
 *
 * @author gqtian
 * @date 2023/10/20 15:07
 */
@Service
public class CompletableFutureParallelDemo {

    @Resource
    @Qualifier("queryThread")
    private ThreadPoolTaskExecutor threadPool;

    /**
     * 3.线程串行化（把后边的线程和前边的串起来）
     */
    public void parent() {
        // thenRunAsync()无返回值
        CompletableFuture<Void> voidCompletableFuture1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("线程号为***" + Thread.currentThread().getId());
            int i = 5;
            System.out.println("---------" + i);
            return i;
        }, threadPool).thenRunAsync(() -> {
            System.out.println("thenRunAsync，不可接受传来的值，自己无返回值的串行化---");
        }, threadPool);

        // thenAccept(x)
        CompletableFuture<Void> voidCompletableFuture2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("线程号为***" + Thread.currentThread().getId());
            int i = 5;
            System.out.println("---------" + i);
            return i;
        }, threadPool).thenAccept((r) -> {
            System.out.println("thenAccept可接受传来的值，自己无返回值的串行化---" + r);
        });

        // thenApply(x)
        CompletableFuture<Integer> voidCompletableFuture3 = CompletableFuture.supplyAsync(() -> {
            System.out.println("线程号为***" + Thread.currentThread().getId());
            int i = 5;
            System.out.println("---------" + i);
            return i;
        }, threadPool).thenApplyAsync((r) -> {
            System.out.println("thenApply可接受传来的值，自己有返回值的串行化---");
            return 10;
        }, threadPool);

    }
}
