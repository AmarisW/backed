/* Copyright (C) 2020-present Linkcld. All rights reserved. */
package com.gqtian.contrast.demo.excelDemo;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

import com.alibaba.excel.EasyExcel;
import com.gqtian.common.globalProcess.GlobalException;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 👻 导出工具类
 *
 * @className ExcelExporter
 * @author gqtian
 * @date 2025/7/29 14:47
 */
@Slf4j
public class ExcelExporter {

    /**
     * 💪 分页写入 (大数据量首选)
     *
     * @param response 响应
     * @param fileName 下载文件名
     * @param sheetName Sheet名称
     * @param dataModel 数据模型
     * @param pageSize 每页数据量
     * @param pageSupplier 分页数据提供器 (你的业务查询逻辑)
     */
    public static <T> void exportByPageSingleSheet(HttpServletResponse response, String fileName, String sheetName,
        Class<T> dataModel, int pageSize, PageQuerySupplier<T> pageSupplier) {

        // 设置响应头
        setupResponse(response, fileName);

        // 分页工具类导出
        try (OutputStream out = response.getOutputStream()) {
            // 委托给核心分页工具执行
            PageWriteExcelHelper.writeByPageSingleSheet(out, dataModel, pageSize, sheetName, pageSupplier);
        } catch (Exception e) {
            // 记录异常日志
            log.error("【数据导出】导出失败: {}", e.getMessage(), e);
            throw new GlobalException("导出失败: " + e.getMessage(), e);
        }
    }

    /** 💪 分页写入 (大数据量) */
    public static <T> void exportByPageMoreSheet(HttpServletResponse response, String fileName, String sheetName,
        Class<T> dataModel, int pageSize, int totalCount, PageQuerySupplier<T> pageSupplier) {
        // 设置响应头
        setupResponse(response, fileName);

        // 分页工具类导出
        try (OutputStream out = response.getOutputStream()) {
            // 委托给核心分页工具执行
            PageWriteExcelHelper.writeByPageMoreSheet(response, out, dataModel, pageSize, totalCount, sheetName,
                pageSupplier);
        } catch (Exception e) {
            // 记录异常日志
            log.error("【数据导出】失败: {}", e.getMessage(), e);
            throw new GlobalException("导出失败: " + e.getMessage(), e);
        }
    }

    /**
     * 💪 普通导出 (小数据量)
     *
     * @param response 响应对象
     * @param fileName 文件名
     * @param sheetName 表格名称
     * @param dataModel 导出数据模型
     * @param dataList 数据列表
     */
    public static <T> void exportSimple(HttpServletResponse response, String fileName, String sheetName,
        Class<T> dataModel, List<T> dataList) {

        setupResponse(response, fileName);

        try (OutputStream out = response.getOutputStream()) {
            EasyExcel.write(out, dataModel).sheet(sheetName).doWrite(dataList);
        } catch (Exception e) {
            // 记录异常日志
            log.error("导出失败: {}", e.getMessage(), e);
            // 当OutputStream已经被获取后，不能再使用getWriter()，直接抛出RuntimeException
            // 让容器处理这个异常，而不是尝试写入错误信息到响应中
            throw new GlobalException("导出失败: " + e.getMessage(), e);
        }
    }

    /**
     * 验证导出数据的完整性 比较响应头中的哈希值和实际响应数据计算出的哈希值是否一致
     *
     * @param responseBytes 响应数据字节数组
     * @param headerHash 响应头中的哈希值
     * @return 是否一致
     */
    public static boolean validateExportData(byte[] responseBytes, String headerHash) {
        try {
            // 计算响应数据的SHA256哈希值
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] computedHashBytes = digest.digest(responseBytes);
            String computedHash = HexFormat.of().formatHex(computedHashBytes);

            // 比较两个哈希值是否一致（忽略大小写）
            return computedHash.equalsIgnoreCase(headerHash);
        } catch (NoSuchAlgorithmException e) {
            throw new GlobalException("不支持的哈希算法: SHA-256", e);
        }
    }

    /**
     * 💪 响应头设置 (复用)
     *
     * @param response 响应对象
     * @param fileName 文件名
     */
    public static void setupResponse(HttpServletResponse response, String fileName) {
        try {
            response.setContentType("text/xlsx; charset=UTF-8"); // 关键：指定 UTF-8
            // 处理空格
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFileName + ".xlsx");
        } catch (Exception e) {
            throw new GlobalException("设置响应头失败", e);
        }
    }
}
