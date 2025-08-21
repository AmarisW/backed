package com.gqtian.fileStorage.demo.multithreadDemo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 🤔 两任务组合
 *
 * @author gqtian
 * @date 2023/10/20 11:15
 */
@Service
public class CompletableFutureBothDemo {
    
    @Resource
    @Qualifier("queryThread")
    private ThreadPoolExecutor threadPool;
    
    /**
     * 异步，两任务组合 ：两个任务都完成，第三个任务才开始执行
     */
    public void both() {
        // 定义两个任务
        // 任务一
        CompletableFuture<Integer> task1 = CompletableFuture.supplyAsync(() -> {
            int i = 5;
            System.out.println("任务一开始执行" + i);
            System.out.println("Thread.currentThread ().getName () = " + Thread.currentThread().getName());
            return i;
        }, threadPool);
        
        // 任务二
        CompletableFuture<Integer> task2 = CompletableFuture.supplyAsync(() -> {
            int i = 10;
            System.out.println("任务二开始执行" + i);
            return i;
        }, threadPool);
        
        // 要求：任务一、二都完成后才执行任务三
        // runAfterBothAsync：无传入值、无返回值
        task1.runAfterBothAsync(task2, () -> {
            System.out.println("任务三开始执行-runAfterBothAsync：无传入值、无返回值  ");
        }, threadPool);
        
        // thenAcceptBothAsync：有传入值、无返回值
        task1.thenAcceptBothAsync(task2, (x, y) -> {
            System.out.println("任务三开始执行-runAfterBothAsync：传入值、无返回值  task1的结果是x ,task2的结果是y" + x + "-----" + y);
        }, threadPool);
        
        // thenCombineAsync：有传入值、有返回值
        task1.thenCombineAsync(task2, (x, y) -> {
            System.out.println("Thread.currentThread ().getName () = " + Thread.currentThread().getName());
            System.out
                .println("任务三开始执行-runAfterBothAsync：无传入值、无返回值  task1的结果是x ,task2的结果是y,task3返回hello" + x + "-----" + y);
            return "hello";
        }, threadPool);
        
        // 异步，两任务组合 ：两个任务都完成其中一个完成，第三个任务才开始执行
        // applyToEitherAsync：有传入值、有返回值
        task1.applyToEitherAsync(task2, (x) -> {
            System.out.println("任务三开始执行-applyToEitherAsync：有传入值、有返回值  task1的结果是x" + x);
            return "hello";
        }, threadPool);
        
        // runAfterEither 无传入值、无返回值 acceptEither 有传入值、无返回值 applyToEither 有传入值、有返回值
        
    }
}
