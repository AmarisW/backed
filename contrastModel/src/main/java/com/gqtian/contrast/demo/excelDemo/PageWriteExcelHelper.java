package com.gqtian.contrast.demo.excelDemo;

import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.gqtian.common.globalProcess.GlobalException;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/***
 * 👻【核心武器】分页写入Excel工具**
 * 
 * @className PageWriteExcelHelper*
 * @author gqtian*
 * @date 2025/7/29 14:43
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PageWriteExcelHelper {

    /** 初始化静态线程池 */
    private static final ThreadPoolExecutor DEAL_EXECUTOR = new ThreadPoolExecutor(
        4, 8, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000), new ThreadFactoryBuilder()
            .setNameFormat("custom-pool-%d").setDaemon(false).setPriority(Thread.NORM_PRIORITY).build(),
        new ThreadPoolExecutor.AbortPolicy());

    /**
     * 执行分页写入（单页写入）
     *
     * @param outputStream 输出流 (响应OutputStream)
     * @param head 数据模型Class (如 User.class)
     * @param pageSize 【重要】每批次处理条数 (建议 1000~5000)
     * @param supplier 分页数据提供器 (你的业务查询逻辑)
     */
    public static <T> void writeByPageSingleSheet(OutputStream outputStream, Class<T> head, int pageSize,
        String sheetName, PageQuerySupplier<T> supplier) throws NoSuchAlgorithmException {
        // 计算数据总数
        Long totalCount = supplier.getTotal();
        // 计数
        AtomicLong atomicLong = new AtomicLong(0);
        // 初始化 SHA-256 哈希计算
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        // 1. 初始化 ExcelWriter (EasyExcel 核心写入器)
        ExcelWriter excelWriter = EasyExcel.write(outputStream, head).excelType(ExcelTypeEnum.XLSX) // 设置导出为 CSV 格式
            .registerWriteHandler(new MyCellWriteHandler(digest))
            .registerWriteHandler(new MyRowWriteHandler(atomicLong)).build();
        WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();
        try {
            // 2. 计算总页数
            int totalPage = totalCount > 0 ? (int)Math.ceil((double)totalCount / pageSize) : 1;
            // 3. 分页循环：查询 -> 写入 -> 释放
            for (int pageNum = 1; pageNum <= totalPage; pageNum++) {
                LocalDateTime start = LocalDateTime.now();
                log.info("【数据导出】第{}页正在处理数据...", pageNum);
                // 3.1 获取当前页数据 (你的分页查询)
                List<T> pageData = supplier.getPage(pageNum, pageSize);
                // 3.2 写入当前页到 Excel
                excelWriter.write(pageData, writeSheet);
                // 3.3 立即清空释放当前页内存！
                pageData.clear();
                log.info("【数据导出】第{}页数据处理完成，耗时{}ms", pageNum, Duration.between(LocalDateTime.now(), start).toMillis());
            }
            // 4.判断写入总数和下载总数是否一致
            if (atomicLong.get() != totalCount) {
                log.error("【数据导出】写入总数和下载总数不一致，请检查！写入总数：{}，下载总数：{}", atomicLong.get(), totalCount);
                throw new GlobalException("写入总数和下载总数不一致，请检查！");
            }
            // 6. 计算最终的 SHA-256 校验值
            byte[] hashBytes = digest.digest();
            String sha256Hex = HexFormat.of().formatHex(hashBytes);
            log.info("【数据导出】本次计算的 SHA-256 校验值：{}", sha256Hex);
        } finally {
            // 4. 【务必关闭】释放资源 (防止内存泄漏)
            if (excelWriter != null) {
                try {
                    excelWriter.finish();
                } catch (Exception e) {
                    log.warn("关闭ExcelWriter时出现异常: ", e);
                }
            }
        }
    }

    /** 执行分页写入（多页写入） */
    public static <T> void writeByPageMoreSheet(HttpServletResponse response, OutputStream outputStream, Class<T> head,
        int pageSize, int totalCount, String sheetName, PageQuerySupplier<T> supplier) {

        // 1. 初始化 ExcelWriter (EasyExcel 核心写入器)
        ExcelWriter excelWriter = EasyExcel.write(outputStream, head).excelType(ExcelTypeEnum.XLSX) // 设置导出为 CSV 格式
            .build();
        try {
            // 2. 计算总页数
            int totalPage = totalCount > 0 ? (int)Math.ceil((double)totalCount / pageSize) : 1;

            // 创建CompletableFuture数组存储每个分页任务
            CompletableFuture[] futures = new CompletableFuture[totalPage];

            // 3. 提交所有分页任务到线程池
            for (int pageNum = 1; pageNum <= totalPage; pageNum++) {
                final int currentPage = pageNum;
                CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
                    log.info("多线程 第{}页正在处理数据...", currentPage);
                    LocalDateTime start = LocalDateTime.now();
                    // 获取当前页数据 (你的分页查询)
                    List<T> pageData = supplier.getPage(currentPage, pageSize);
                    log.info("多线程 第{}页数据处理完成，耗时{}ms", currentPage,
                        Duration.between(LocalDateTime.now(), start).toMillis());
                    // 写入当前页到 Excel
                    synchronized (excelWriter) {
                        WriteSheet writeSheet = EasyExcel.writerSheet(sheetName + currentPage).build();
                        excelWriter.write(pageData, writeSheet);
                        pageData.clear();
                    }
                }, DEAL_EXECUTOR);
                // 添加到futures数组中
                futures[pageNum - 1] = completableFuture;
            }

            // 4. 等待所有任务完成
            CompletableFuture.allOf(futures).join();
        } catch (Exception e) {
            log.error("处理数据时出现异常", e);
            throw new RuntimeException("处理数据时出现异常", e);
        } finally {
            // 4. 【务必关闭】释放资源 (防止内存泄漏)
            if (excelWriter != null) {
                try {
                    excelWriter.finish();
                } catch (Exception e) {
                    log.warn("关闭ExcelWriter时出现异常: ", e);
                }
            }
        }
    }

    /**
     * 💪 根据系统合理计算出 单页多少条数据
     *
     * @return int
     */
    public static int calculateOptimalPageSize() {
        // 1. 估算单条数据大小 (字节) - 根据业务模型调整逻辑
        long approxBytesPerRow = 500;

        // 2. 获取当前JVM可用内存 (转成字节)
        long freeMemoryBytes = Runtime.getRuntime().freeMemory();

        // 3. 【安全策略】仅使用一部分可用内存 (例如 40%)
        long safeMemoryToUse = (long)(freeMemoryBytes * 0.4);

        // 4. 计算建议分页条数
        int suggestedPageSize = (int)(safeMemoryToUse / approxBytesPerRow);

        // 5. 设置合理范围 (防止太大或太小) 限制在1000~10000条/页
        return Math.max(1000, Math.min(suggestedPageSize, 10000));
    }
}
