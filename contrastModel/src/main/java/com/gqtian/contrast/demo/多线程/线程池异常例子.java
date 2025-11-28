package com.gqtian.contrast.demo.多线程;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 🤔 线程池异常例子
 *
 * @author gqtian
 * @date 2023/08/25 16:38
 */
public class 线程池异常例子 {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
            // 1、submit()方法提交任务，任务执行异常，会被线程池吃掉，不会抛出异常
            // executor.submit(new DivTask(100, i));

            // 2、submit()方法提交任务，任务执行异常，会抛出异常
            Future<?> future = executor.submit(new DivTask(100, i));
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static class DivTask implements Runnable {
        int a, b;

        public DivTask(int a, int b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public void run() {
            double result = (double)a / b;
            System.out.println(result);
        }
    }
}
