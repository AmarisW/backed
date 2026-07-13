package com.gqtian.contrast.demo.多线程;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 🤔 两任务组合
 * 
 * 演示CompletableFuture的双任务组合执行方式：
 * 1. runAfterBothAsync - 两个任务都完成后执行无输入无输出的任务
 * 2. thenAcceptBothAsync - 两个任务都完成后执行有输入无输出的任务
 * 3. thenCombineAsync - 两个任务都完成后执行有输入有输出的任务
 * 4. applyToEitherAsync - 两个任务任一完成后执行有输入有输出的任务
 *
 * @author gqtian
 * @date 2023/10/20 11:15
 */
@Service
public class FutureBothDemo {

    @Resource
    private ThreadPoolTaskExecutor threadPool;

    /**
     * 异步，两任务组合 ：两个任务都完成，第三个任务才开始执行
     * 
     * 演示多种双任务组合执行方式：
     * 1. runAfterBothAsync - 不接收两个前置任务的结果，也不产生新结果
     * 2. thenAcceptBothAsync - 接收两个前置任务的结果，但不产生新结果
     * 3. thenCombineAsync - 接收两个前置任务的结果，并产生新结果
     * 4. applyToEitherAsync - 任一前置任务完成后执行，接收该任务结果并产生新结果
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
        // 适用于两个前置任务都完成后需要执行一些不依赖其结果的操作
        task1.runAfterBothAsync(task2, () -> {
            System.out.println("任务三开始执行-runAfterBothAsync：无传入值、无返回值  ");
        }, threadPool);

        // thenAcceptBothAsync：有传入值、无返回值
        // 适用于需要使用两个前置任务的结果但不需要返回新结果的场景
        task1.thenAcceptBothAsync(task2, (x, y) -> {
            System.out.println("任务三开始执行-runAfterBothAsync：传入值、无返回值  task1的结果是x ,task2的结果是y" + x + "-----" + y);
        }, threadPool);

        // thenCombineAsync：有传入值、有返回值
        // 适用于需要基于两个前置任务的结果计算并返回新结果的场景
        task1.thenCombineAsync(task2, (x, y) -> {
            System.out.println("Thread.currentThread ().getName () = " + Thread.currentThread().getName());
            System.out
                .println("任务三开始执行-runAfterBothAsync：无传入值、无返回值  task1的结果是x ,task2的结果是y,task3返回hello" + x + "-----" + y);
            return "hello";
        }, threadPool);

        // 异步，两任务组合 ：两个任务都完成其中一个完成，第三个任务才开始执行
        // applyToEitherAsync：有传入值、有返回值
        // 适用于只需要任一前置任务完成就可以继续执行的场景
        task1.applyToEitherAsync(task2, (x) -> {
            System.out.println("任务三开始执行-applyToEitherAsync：有传入值、有返回值  task1的结果是x" + x);
            return "hello";
        }, threadPool);

        // runAfterEither 无传入值、无返回值 acceptEither 有传入值、无返回值 applyToEither 有传入值、有返回值

    }
}