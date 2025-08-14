/* Copyright (C) 2020-present Linkcld. All rights reserved. */
package com.gqtian.fileStorage.demo.excel;

import java.util.List;

/**
 * 👻 内部接口：分页查询供应商
 *
 * @className PageQuerySupplier
 * @author gqtian
 * @date 2025/7/29 15:18
 */
@FunctionalInterface // 函数式接口只能有一个抽象方法，但允许有多个默认方法或静态方法：
public interface PageQuerySupplier<T> {
    
    List<T> getPage(int pageNum, int pageSize); // 函数式接口
}
