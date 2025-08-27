package com.gqtian.contrast.controller;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.gqtian.common.annotation.GlobalApiOut;
import com.gqtian.common.globalProcess.GlobalException;
import com.gqtian.contrast.demo.excelDemo.ExcelExporter;
import com.gqtian.contrast.entity.SystemUser;
import com.gqtian.contrast.service.SystemUserService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 👻 测试控制器
 * 
 * @className TestController
 * @author gqtian
 * @date 2025/8/15 00:00
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/excel")
public class ExcelController {
    private final SystemUserService globalUserService;

    @GlobalApiOut("数据导出")
    @GetMapping("/export")
    public void export(HttpServletResponse response) {
        try {
            // 数据导出
            log.info("【数据导出】开始......");
            LocalDateTime start = LocalDateTime.now();
            ExcelExporter.exportByPageSingleSheet(response, "数据文件", "网约车基础数据", SystemUser.class, 1000,
                globalUserService);
            log.info("【数据导出】完成，耗时{}ms", Duration.between(LocalDateTime.now(), start).toMillis());
        } catch (Exception e) {
            log.error("【数据导出】异常：{}", e.getMessage());
            throw new GlobalException(e.getMessage());
        }
    }

    /**
     * 测试数据导入
     */
    @GlobalApiOut("数据导入")
    @PostMapping("/import")
    public void importData(@RequestParam("file") MultipartFile file, @RequestBody SystemUser systemUser) {
        try {
            EasyExcel.read(file.getInputStream()).head(SystemUser.class).excelType(ExcelTypeEnum.XLSX).sheet()
                .doReadSync();
        } catch (Exception e) {
            log.error("【数据导入】异常：{}", e.getMessage());
        }
    }
}
