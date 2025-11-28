package com.gqtian.contrast.demo.多线程;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 🤔 组合
 *
 * @author gqtian
 * @date 2023/10/20 15:18
 */
@Service
public class CompletableFutureCombinationDemo {

    @Resource
    @Qualifier("queryThread")
    private ThreadPoolTaskExecutor threadPool;

    CompletableFuture<String> findAccount(String accountId) {
        return CompletableFuture.supplyAsync(() -> {
            // mock finding account from database
            return "account+" + accountId;
        });
    }

    public void batchProcess(List<String> accountIdList) {
        // 并行根据accountId查找对应account
        List<CompletableFuture<String>> accountFindingFutureList =
            accountIdList.stream().map(this::findAccount).toList();

        // 使用allOf方法来表示所有的并行任务
        CompletableFuture<Void> allFutures =
            CompletableFuture.allOf(accountFindingFutureList.toArray(new CompletableFuture[0]));
        // .anyOf (accountFindingFutureList.toArray (new CompletableFuture[accountFindingFutureList.size ()]));

        // 下面的方法可以帮助我们获得所有子任务的处理结果
        CompletableFuture<List<String>> finalResults = allFutures.thenApply(
            v -> accountFindingFutureList.stream().map(CompletableFuture::join).collect(Collectors.toList()));
        finalResults.getNow(accountIdList).forEach(System.out::println);
    }

}
