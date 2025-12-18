package com.gqtian.hbase.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gqtian.hbase.service.HBaseSearchService;

import lombok.RequiredArgsConstructor;

/**
 * HBase 数据查询和处理控制器
 * 
 * 提供 HBase 数据的数据修正、数据发送等功能接口
 *
 * @className HbaseController
 * @author gqtian
 * @date 2024/7/4 1:13
 */
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class HbaseController {

    // HBase 搜索服务
    private final HBaseSearchService hBaseSearchService;

    /**
     * 数据修正接口
     * 
     * 根据指定的时间范围对 HBase 中的数据进行修正处理
     *
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param pn 分页参数 - 页码
     * @param pc 分页参数 - 每页行数
     */
    @RequestMapping("/correction")
    public void test(String beginTime, String endTime, String pn, String pc) {
        hBaseSearchService.correction(beginTime, endTime);
    }

    /**
     * 数据发送接口
     * 
     * 将处理后的数据发送到下游系统
     */
    @RequestMapping("/send")
    public void save() {
        hBaseSearchService.send();
    }
}
