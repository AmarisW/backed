package com.gqtian.fileStorage.demo.multithreadDemo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 🤔 单任务异步
 *
 * @author gqtian
 * @date 2023/10/20 15:15
 */
@Service
public class CompletableFutureSingleDemo {
    @Resource
    @Qualifier("testThread")
    private ThreadPoolExecutor threadPool;
    
    public void single() {
        // 1.无返回值的异步任务 runAsync()
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
            System.out.println("线程号为***" + Thread.currentThread().getId());
            int i = 5;
            System.out.println("---------" + i);
        }, threadPool);
        
        // 2.有返回值异步任务 supplyAsync()
        CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("线程号为***" + Thread.currentThread().getId());
            int i = 5;
            System.out.println("---------" + i);
            return i;
        }, threadPool).whenComplete((r, e) -> {
            // whenComplete第一个参数是结果，第二个参数是异常，他可以感知异常，无法返回默认数据
            System.out.println("执行完毕，结果是---" + r + "异常是----" + e);
        }).exceptionally(u -> {
            // exceptionally只有一个参数是异常类型，他可以感知异常，同时返回默认数据10
            return 10;
            // handler既可以感知异常，也可以返回默认数据，是whenComplete和exceptionally的结合
        }).handle((r, e) -> {
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
