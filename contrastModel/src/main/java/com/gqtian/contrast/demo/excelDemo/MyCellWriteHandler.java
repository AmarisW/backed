/* Copyright (C) 2020-present Linkcld. All rights reserved. */
package com.gqtian.contrast.demo.excelDemo;

import java.security.MessageDigest;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.handler.CellWriteHandler;
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
public class MyCellWriteHandler implements CellWriteHandler {

    private final MessageDigest digest;

    public MyCellWriteHandler(MessageDigest digest) {
        this.digest = digest;
    }

    @Override
    public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder,
        List<WriteCellData<?>> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
        // 标题行空行不参与签名计算
        if (cell.getRowIndex() == 0 || cell.getStringCellValue() == null) {
            return;
        }
        digest.update(cell.getStringCellValue().getBytes());
    }
}
