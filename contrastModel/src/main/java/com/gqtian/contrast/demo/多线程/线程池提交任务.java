package com.gqtian.contrast.demo.多线程;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 🤔 线程池提交任务
 * 
 * 线程池提供两种提交任务的方式:
 * 1. execute(Runnable) - 执行无返回值的任务,来自Executor接口
 * 2. submit(Callable/Runnable) - 执行有返回值的任务,来自ExecutorService接口,返回Future对象
 *
 * @author gqtian
 * @date 2023/08/25 16:32
 */
public class 线程池提交任务 {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 创建一个包含2个线程的固定大小线程池
        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        /*
         * 方式1: execute() - 提交无返回值的任务
         * 特点:
         *   - 参数类型: Runnable
         *   - 返回值: void,无法获取任务执行结果
         *   - 异常处理: 任务中的异常会直接抛出
         *   - 适用场景: 不关心任务执行结果的场景,如日志记录、消息通知等
         */
        executor.execute(() -> System.out.println("hello"));

        /*
         * 方式2: submit() - 提交有返回值的任务
         * 特点:
         *   - 参数类型: Callable<T> 或 Runnable
         *   - 返回值: Future<T>,可以通过Future获取任务执行结果
         *   - 异常处理: 任务中的异常会被封装在Future中,调用get()时抛出
         *   - 适用场景: 需要获取任务执行结果或监控任务状态的场景
         */
        Future<Long> future = executor.submit(() -> {
            System.out.println("task is executed");
            // 返回任务执行时的时间戳
            return System.currentTimeMillis();
        });
        
        // 调用future.get()会阻塞当前线程,直到任务执行完成并返回结果
        System.out.println("task execute time is: " + future.get());
        
        // 关闭线程池,不再接受新任务
        executor.shutdown();
    }
}
