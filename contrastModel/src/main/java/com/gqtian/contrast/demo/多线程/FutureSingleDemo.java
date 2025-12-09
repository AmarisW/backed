package com.gqtian.contrast.demo.多线程;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 🤔 单任务异步
 * 
 * 演示CompletableFuture的单任务异步执行方式：
 * 1. runAsync() - 无返回值的异步任务
 * 2. supplyAsync() - 有返回值的异步任务
 * 3. 异常处理机制 - whenComplete, exceptionally, handle
 *
 * @author gqtian
 * @date 2023/10/20 15:15
 */
@Service
public class FutureSingleDemo {

    @Resource
    @Qualifier("queryThread")
    private ThreadPoolTaskExecutor threadPool;

    /**
     * 演示单个异步任务的执行方式和异常处理
     * 包括无返回值任务、有返回值任务以及多种异常处理方式
     */
    public void single() {
        // 1.无返回值的异步任务 runAsync()
        // 适用于不需要返回结果的场景，如日志记录、发送通知等
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
            System.out.println("线程号为***" + Thread.currentThread().getId());
            int i = 5;
            System.out.println("---------" + i);
        }, threadPool);

        // 2.有返回值异步任务 supplyAsync()
        // 适用于需要获取任务执行结果的场景
        CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("线程号为***" + Thread.currentThread().getId());
            int i = 5;
            System.out.println("---------" + i);
            return i;
        }, threadPool)
            // whenComplete第一个参数是结果，第二个参数是异常，他可以感知异常，无法返回默认数据
            // 适用于仅需要记录任务执行状态而不改变结果的场景
            .whenComplete((r, e) -> {
                System.out.println("执行完毕，结果是---" + r + "异常是----" + e);
            })
            // exceptionally只有一个参数是异常类型，他可以感知异常，同时返回默认数据10
            // 适用于需要在任务失败时提供默认返回值的场景
            .exceptionally(u -> {
                return 10;
            })
            // handler既可以感知异常，也可以返回默认数据，是whenComplete和exceptionally的结合
            // 适用于需要根据任务执行结果或异常情况来决定返回值的场景
            .handle((r, e) -> {
                if (r != null) {
                    return r;
                }
                if (e != null) {
                    return 0;
                }
                return 0;
            });
    }
}