package com.gqtian.mybatis.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gqtian.mybatis.entity.BookSlave;

/**
* @author gqtia
* @description 针对表【books(图书信息表)】的数据库操作Service
* @createDate 2025-12-23 15:47:20
*/
public interface BookSlaveService extends IService<BookSlave> {

    Page<BookSlave> pageLambdaSlave(Page<BookSlave> page, String title);
}
