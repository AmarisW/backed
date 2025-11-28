/* Copyright (C) 2020-present Linkcld. All rights reserved. */
package com.gqtian.contrast.demo.easyExcel;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.poi.ss.usermodel.Row;

import com.alibaba.excel.write.handler.RowWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;

import lombok.extern.slf4j.Slf4j;

/**
 * 👻 cell控制器
 *
 * @className MyCellWriteHandler
 * @author gqtian
 * @date 2025/7/30 16:52
 */
@Slf4j
public class MyRowWriteHandler implements RowWriteHandler {

    private final AtomicLong atomicLong;

    public MyRowWriteHandler(AtomicLong atomicLong) {
        this.atomicLong = atomicLong;
    }

    @Override
    public void afterRowDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row,
        Integer relativeRowIndex, Boolean isHead) {
        if (row.getRowNum() == 0) {
            return;
        }
        atomicLong.incrementAndGet();
    }
}
