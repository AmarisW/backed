package com.gqtian.contrast.demo.多线程;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Lists;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CompletableFutureDemo {

    /**
     * CPU核数
     */
    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(AVAILABLE_PROCESSORS, // 核心线程数
        3 * AVAILABLE_PROCESSORS, // 最大线程数
        3, TimeUnit.SECONDS, // keepAliveTime
        new LinkedBlockingDeque<>(20)); // 阻塞队列

    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("🚀 demo start.....{}", startTime);

        // demo();
        // demo1();
        // demo2();
        // demo3();
        // demo4();

        // resultGet
        resultGet();
        log.info("✅ demo end.....costTime = {}", (System.currentTimeMillis() - startTime));

    }

    private static void demo() {
        // 用于整体接收各个任务的返回值
        Map<String, Object> dataMap = new ConcurrentHashMap<>();
        doSomething1("doSomething1", dataMap);
        doSomething2("doSomething2", dataMap);
        doSomething3("doSomething3", dataMap);
        log.info("📦 dataMap = {}", dataMap);
    }

    /**
     * 基于allOf，并行处理多个任务，等待所有任务执行完毕后返回
     */
    public static void demo3() {
        // 用于整体接收各个任务的返回值
        Map<String, Object> dataMap = new ConcurrentHashMap<>();

        List<CompletableFuture<String>> futureList = new ArrayList<>();
        futureList.add(doSomethingA("A", dataMap));
        futureList.add(doSomethingB("B", dataMap));
        futureList.add(doSomethingC("C", dataMap));

        CompletableFuture<Void> result = CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0]));

        try {
            result.join();
            result.get();
        } catch (Exception e) {
            log.error("❌ Exception occurred: ", e);
        }

        log.info("📦 result = {}", dataMap);
        // 结果为：{doSomeThingB=B, doSomeThingA=A}
    }

    /**
     * 基于thenCompose，第一个任务执行完后，第二个任务使用第一个任务的返回作为参数
     */
    public static void demo1() throws Exception {
        Map<String, Object> dataMap = new ConcurrentHashMap<>();

        CompletableFuture<String> completableFuture =
            doSomethingA("A", dataMap).thenCompose(id -> doSomethingB(id, dataMap));

        String result = completableFuture.get(2, TimeUnit.SECONDS);
        log.info("📦 result = {}", result);
        // 结果为：A is done is done
    }

    /**
     * 基于thenCombine，当两个任务都完成后，使用两者的结果作为参数再执行一个异步任务
     */
    public static void demo2() throws Exception {
        Map<String, Object> dataMap = new ConcurrentHashMap<>();

        CompletableFuture<String> completableFuture =
            doSomethingA("A", dataMap).thenCombine(doSomethingB("B", dataMap), (a, b) -> a + " - " + b);

        String result = completableFuture.get(2, TimeUnit.SECONDS);
        log.info("📦 result = {}", result);
        // 结果为：A is done - B is done
    }

    /**
     * 使用CompletionService批量异步处理
     *
     * CompletionService 能够让异步任务的执行结果有序化，先执行完的先进入阻塞队列， 利用这个特性，你可以轻松实现后续处理的有序性，避免无谓的等待
     */
    public static void demo4() {
        Map<String, Object> dataMap = new ConcurrentHashMap<>();

        CompletionService<Map> cs = new ExecutorCompletionService<>(threadPoolExecutor);
        cs.submit(() -> doSomething1("doSomething1", dataMap));
        cs.submit(() -> doSomething2("doSomething2", dataMap));
        cs.submit(() -> doSomething3("doSomething3", dataMap));

        for (int i = 0; i < 3; i++) {
            Map resultMap = null;
            try {
                // 依次从队列中取任务执行结果
                resultMap = cs.take().get();
            } catch (Exception e) {
                log.error("❌ Exception occurred: ", e);
            }
            log.info("📦 resultMap = {}", resultMap);
        }
    }

    /**
     * @param dataMap 用于整体接收方法的返回值
     */
    public static CompletableFuture<String> doSomethingA(String taskId, Map<String, Object> dataMap) {
        log.info("🔄 doSomethingA start.....{}", System.currentTimeMillis());

        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error("❌ InterruptedException in doSomethingA: ", e);
            }
            dataMap.put("doSomeThingA", "A");
            return taskId + " is done";
        }, threadPoolExecutor);
    }

    public static CompletableFuture<String> doSomethingB(String taskId, Map<String, Object> dataMap) {
        log.info("🔄 doSomethingB start.....{}", System.currentTimeMillis());

        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error("❌ InterruptedException in doSomethingB: ", e);
            }
            dataMap.put("doSomeThingB", "B");
            return taskId + " is done";

        }, threadPoolExecutor);
    }

    public static CompletableFuture<String> doSomethingC(String taskId, Map<String, Object> dataMap) {
        log.info("🔄 doSomethingC start.....{}", System.currentTimeMillis());

        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error("❌ InterruptedException in doSomethingC: ", e);
            }
            dataMap.put("doSomeThingC", "C");
            return taskId + " is done";

        }, threadPoolExecutor);
    }

    public static Map<String, Object> doSomething1(String taskId, Map<String, Object> dataMap) {
        log.info("🔄 doSomething1 start.....{}", System.currentTimeMillis());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error("❌ InterruptedException in doSomething1: ", e);
        }
        dataMap.put(taskId, "doSomething1");
        return dataMap;
    }

    public static Map<String, Object> doSomething2(String taskId, Map<String, Object> dataMap) {
        log.info("🔄 doSomething2 start.....{}", System.currentTimeMillis());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error("❌ InterruptedException in doSomething2: ", e);
        }
        dataMap.put(taskId, "doSomething2");
        return dataMap;
    }

    public static Map<String, Object> doSomething3(String taskId, Map<String, Object> dataMap) {
        log.info("🔄 doSomething3 start.....{}", System.currentTimeMillis());

        try {
            int i = Integer.parseInt("");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error("❌ InterruptedException in doSomething3: ", e);
        } catch (NumberFormatException e) {
            log.error("❌ NumberFormatException in doSomething3: ", e);
        }
        dataMap.put(taskId, "doSomething3");
        return dataMap;
    }

    // ===========================================================================================//

    public static void resultGet() {
        long start = System.currentTimeMillis();
        // 结果集
        List<Integer> list = new ArrayList<>();
        // 错误任务集合
        List<Integer> errList = new ArrayList<>();
        // 定长10线程池
        List<CompletableFuture<String>> futureList = new ArrayList<>();
        final List<Integer> taskList = Lists.newArrayList(2, 1, 3, 4, 5, 6, 7, 8, 9, 10);
        try {
            //// 方式一：循环创建CompletableFuture list,调用sequence()组装返回一个有返回值的CompletableFuture，返回结果get()获取
            // for(int i=0;i<taskList.size();i++){
            // final int j=i;
            // //异步执行
            // CompletableFuture<String> future = CompletableFuture.supplyAsync(()->calc(taskList.get
            // (j)), exs)
            // //Integer转换字符串 thenAccept只接受不返回不影响结果
            // .thenApply(e->Integer.toString(e))
            // //如需获取任务完成先后顺序，此处代码即可
            // .whenComplete((v, e) -> {
            // log.info("任务{}完成!result={},异常 e={},{}", v, v, e, new Date());
            // list2.add(v);
            // })
            // ;
            // futureList.add(future);
            // }
            //// 流式获取结果：此处是根据任务添加顺序获取的结果
            // list = sequence(futureList).get();

            // 方式二：全流式处理转换成CompletableFuture[]+组装成一个无返回值CompletableFuture，join等待执行完毕。返回结果whenComplete获取
            CompletableFuture[] array = taskList.stream()
                .map(object -> CompletableFuture.supplyAsync(() -> calc(object), threadPoolExecutor)
                    // 如需获取任务完成先后顺序，此处代码即可
                    .whenComplete((v, e) -> {
                        if (e != null) {
                            // 在异常情况下，使用 object 或其他标识符代替 v
                            log.error("❌ 任务【{}】发生异常 e=【{}】,【{}】", object, e.getMessage(),
                                DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                            errList.add(object);
                        } else {
                            // 在正常情况下，v 包含 calc(object) 的返回值
                            log.info("✅ 任务【{}】完成!result=【{}】,【{}】", v, v,
                                DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                            list.add(object);
                        }
                    })

                ).toArray(CompletableFuture[]::new);
            // 等待总任务完成，但是封装后无返回值，必须自己whenComplete()获取 等待所有任务执行完成
            CompletableFuture.allOf(array).join();

        } catch (Exception e) {
            log.error("❌ exception = ", e);
        }
        // finally
        // {
        // threadPoolExecutor.shutdown();
        // }
        // TODO 放在此处只是为了方便观察结果,想打印出相关结果，必须等待任务全部完成/且放在try-catch外面
        log.warn("⚠️ 错误任务结果集合，结果errList=【{}】", errList);
        log.warn("⚠️ 任务完成先后顺序，结果list=【{}】；任务提交顺序,耗时=【{}】", list, (System.currentTimeMillis() - start));
    }

    public static Integer calc(Integer i) {
        try {
            if (i == 1) {
                // 任务1耗时3秒
                Thread.sleep(3000);
            } else if (i == 5) {
                // 抛出异常
                // 任务5耗时5秒
                Thread.sleep(5000);
                throw new RuntimeException("任务5抛出异常");
            } else {
                // 其它任务耗时1秒
                Thread.sleep(1000);
            }
            log.info("✅ Task线程：【{}】 任务i= 【{}】,完成！【{}】", Thread.currentThread().getName(), i,
                DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        } catch (InterruptedException e) {
            log.error("❌ Task线程：【{}】 任务i= 【{}】,中断！【{}】", Thread.currentThread().getName(), i,
                DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        }
        return i * 10;
    }

    /**
     *
     * @Description 组合多个CompletableFuture为一个CompletableFuture, 所有子任务全部完成，组合后的任务才会完成。带返回值，可直接get.
     * @param futures List
     */
    public static <T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> futures) {
        // 1.构造一个空CompletableFuture，子任务数为入参任务list size
        CompletableFuture<Void> allDoneFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        // 2.流式（总任务完成后，每个子任务join取结果，后转换为list）
        return allDoneFuture.thenApply(v -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList()));
    }

    /**
     *
     * @Description Stream流式类型futures转换成一个CompletableFuture, 所有子任务全部完成，组合后的任务才会完成。带返回值，可直接get.
     * @param futures Stream
     */
    public static <T> CompletableFuture<List<T>> sequence(Stream<CompletableFuture<T>> futures) {
        List<CompletableFuture<T>> futureList = futures.filter(Objects::nonNull).collect(Collectors.toList());
        return sequence(futureList);
    }

}
