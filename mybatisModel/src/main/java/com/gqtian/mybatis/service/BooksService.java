package com.gqtian.mybatis.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gqtian.mybatis.entity.Books;
import com.gqtian.mybatis.entity.params.QueryParam;

/**
* @author gqtia
* @description 针对表【books(图书信息表)】的数据库操作Service
* @createDate 2025-12-23 15:47:20
*/
public interface BooksService extends IService<Books> {

  Page<Books> pageLambda(Page<Books> page, String  title);

  Page<Books> pageXml(Page<Books> page,  QueryParam param);
}
