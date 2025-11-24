package com.gqtian.network.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import com.gqtian.network.cache.SystemCache;
import com.gqtian.network.request.entity.RequestHead;
import com.gqtian.network.request.entity.RequestParam;
import com.gqtian.network.request.entity.ResponseResult;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 🤨 请求工具类
 * @className RequestUtil
 * @author gqtian
 * @date 2025/11/24 14:40
 */
@Slf4j
public class RequestUtil {

    /**
     * 通用分页数据获取方法
     *
     * @param requestParam 基础请求参数
     * @param requestFunc 请求函数，用于执行具体的数据请求
     * @param <T> 数据项类型
     * @return 数据项列表
     */
    private <T> List<T> fetchDataWithPage(RequestParam requestParam,
        BiFunction<RequestParam, RequestHead, ResponseResult<List<T>>> requestFunc) {
        List<T> resultList = new ArrayList<>();
        // 初始化 页码
        int pageNo = 1;
        // 每页数量
        final int pageSize = 100;
        boolean hasMoreData = true;

        while (hasMoreData) {
            try {
                // 分页参数
                requestParam.setPageNo(pageNo);
                requestParam.setPageSize(pageSize);
                // 头部参数
                RequestHead head = RequestHead.builder().authorization(SystemCache.AUTHORIZATION).build();
                ResponseResult<List<T>> result = requestFunc.apply(requestParam, head);
                // 如果没有数据则直接退出循环
                if (result.getData() == null || CollectionUtil.isEmpty(result.getData())) {
                    hasMoreData = false;
                } else {
                    resultList.addAll(result.getData());
                    pageNo++;
                }
            } catch (Exception e) {
                log.error("数据请求异常，异常信息: {}", e.getMessage());
                // 退出循环
                hasMoreData = false;
            }
        }
        return resultList;
    }
}
