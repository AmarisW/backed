package com.gqtian.mybatis.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gqtian.mybatis.entity.Book;
import com.gqtian.mybatis.entity.params.QueryParam;
import com.gqtian.mybatis.entity.vo.BookVo;

/**
* @author gqtia
* @description 针对表【books(图书信息表)】的数据库操作Service
* @createDate 2025-12-23 15:47:20
*/
public interface BookService extends IService<Book> {

    Page<Book> pageLambda(Page<Book> page, String title);

  Page<Book> pageXml(Page<Book> page, QueryParam param);

  List<BookVo> nestedQueryXml(QueryParam param);

  List<BookVo> nestedResultXml(QueryParam param);
}
