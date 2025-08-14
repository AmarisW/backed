package com.gqtian.fileStorage.controller;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gqtian.common.globalProcess.GlobalException;
import com.gqtian.fileStorage.demo.excel.ExcelExporter;
import com.gqtian.fileStorage.entity.GlobalUser;
import com.gqtian.fileStorage.service.GlobalUserService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 👻 测试
 * 
 * @className TestController
 * @author gqtian
 * @date 2025/8/15 00:00
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {
    private final GlobalUserService globalUserService;
    
    @RequestMapping("/export")
    public void export(HttpServletResponse response) {
        try {
            // 数据导出
            log.info("【数据导出】开始......");
            LocalDateTime start = LocalDateTime.now();
            ExcelExporter
                .exportByPageSingleSheet(response, "数据文件", "网约车基础数据", GlobalUser.class, 100000, 5, globalUserService);
            log.info("【数据导出】完成，耗时{}ms", Duration.between(LocalDateTime.now(), start).toMillis());
        }
        catch (Exception e) {
            log.error("【数据导出】异常：{}", e.getMessage());
            throw new GlobalException(e.getMessage());
        }
    }
}
