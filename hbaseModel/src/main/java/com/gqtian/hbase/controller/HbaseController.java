package com.gqtian.hbase.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gqtian.hbase.service.HBaseSearchService;

import lombok.RequiredArgsConstructor;

/**
 * 👻
 *
 * @className HbaseController
 * @author gqtian
 * @date 2024/7/4 1:13
 */
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class HbaseController {

    private final HBaseSearchService hBaseSearchService;

    @RequestMapping("/correction")
    public void test(String beginTime, String endTime, String pn, String pc) {
        hBaseSearchService.correction(beginTime, endTime);
    }

    @RequestMapping("/send")
    public void save() {
        hBaseSearchService.send();
    }
}
