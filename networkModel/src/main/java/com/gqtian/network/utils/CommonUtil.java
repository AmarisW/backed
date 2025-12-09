package com.gqtian.network.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.gqtian.network.cache.SystemCache;
import com.gqtian.network.request.entity.RequestHead;
import com.gqtian.network.request.entity.RequestParam;
import com.gqtian.network.request.entity.ResponseResult;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 🤨 请求工具类
 * @className RequestUtil
 * @author gqtian
 * @date 2025/11/24 14:40
 */
@Slf4j
@Component
public class CommonUtil {

    /**
     * 通用分页数据获取方法
     *
     * @param requestParam 基础请求参数
     * @param requestFunc 请求函数，用于执行具体的数据请求
     * @param <T> 数据项类型
     * @return 数据项列表
     */
    public static <T> List<T> fetchDataWithPage(RequestParam requestParam,
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

    /**
     * 批量保存数据
     *
     * @param dataList 数据列表
     * @param saveFunction 保存函数
     * @param <T>
     */
    public static <T> void saveBatch(String message, List<T> dataList, Function<List<T>, Boolean> saveFunction) {
        if (CollectionUtil.isEmpty(dataList)) {
            log.info("数据列表为空，不进行保存");
            return;
        }

        int batchSize = 500;
        int total = dataList.size();
        int fromIndex = 0;
        int batchNumber = 1;

        log.info("{} 开始批量保存数据，总记录数: {}, 总批次数: {}", message, total, batchSize);

        while (fromIndex < total) {
            int toIndex = Math.min(fromIndex + batchSize, total);
            List<T> batch = dataList.subList(fromIndex, toIndex);
            try {
                Boolean saveResult = saveFunction.apply(batch);
                if (saveResult) {
                    log.info("{} 第{}批次保存成功，记录数: {}", message, batchNumber, batch.size());
                } else {
                    log.error("{} 第{}批次保存失败，记录数: {}", message, batchNumber, batch.size());
                }
            } catch (Exception e) {
                log.error("{} 第{}批次保存失败，记录数: {}, 错误信息: {}", message, batchNumber, batch.size(), e.getMessage());
            } finally {
                fromIndex += batchSize;
                batchNumber++;
            }
        }

        log.info("{} 所有数据批量保存完成，总记录数: {}", message, total);
    }

    /**
     * 处理时间参数，如果为空则使用默认时间（昨天一整天）
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 包含开始时间和结束时间的数组，[0]为开始时间，[1]为结束时间
     */
    public static String[] processTimeParams(String startTime, String endTime) {

        Date yesterday = DateUtil.yesterday();
        String defaultStartTime = DateUtil.format(DateUtil.beginOfDay(yesterday), DatePattern.NORM_DATE_PATTERN);
        String defaultEndTime = DateUtil.format(DateUtil.endOfDay(yesterday), DatePattern.NORM_DATE_PATTERN);

        // 如果传参失败则传默认时间
        if (StringUtils.isEmpty(startTime)) {
            startTime = defaultStartTime;
        }
        if (StringUtils.isEmpty(endTime)) {
            endTime = defaultEndTime;
        }

        return new String[] {startTime, endTime};
    }
}
