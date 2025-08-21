package com.gqtian.fileStorage.demo.multithreadDemo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 🤔 我们异步执行一个任务时，一般是用线程池Executor去创建。如果不需要有返回值，
 * 任务实现Runnable接口；如果需要有返回值，任务实现Callable接口，调用Executor的submit方法，再使用Future获取即可。如果多个线程存在依赖组合的话，我们怎么处理呢？可使用同步组件CountDownLatch、CyclicBarrier等，但是比较麻烦。其实有简单的方法，就是用CompeletableFuture。
 *
 * @author gqtian
 * @date 2023/08/25 16:42
 */
public class 异步编程 {
    
    public static void main(String[] args)
        throws ExecutionException, InterruptedException {
        demo1();
    }
    
    /**
     * 👻 1、异步操作例子
     * 
     */
    static void demo1()
        throws ExecutionException, InterruptedException {
        Runnable runnable = () -> System.out.println("无返回结果异步任务");
        CompletableFuture.runAsync(runnable);
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("有返回值的异步任务");
            try {
                Thread.sleep(5000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Hello World";
        });
        String result = future.get();
    }
    
}
