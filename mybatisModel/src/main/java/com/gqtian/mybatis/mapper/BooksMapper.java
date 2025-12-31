package com.gqtian.mybatis.mapper;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gqtian.mybatis.entity.Books;
import com.gqtian.mybatis.entity.params.QueryParam;

/**
* @author gqtia
* @description 针对表【books(图书信息表)】的数据库操作Mapper
* @createDate 2025-12-23 15:47:20
* @Entity com.gqtian.mybatis.entity.Books
*/
public interface BooksMapper extends BaseMapper<Books> {

    /**
     * 使用 XML 自定义 SQL 进行分页查询
     * 
     * @param page 分页对象
     * @param param 参选参数
     * @return 分页结果
     */
    Page<Books> pageXml(@Param("page") Page<Books> page, @Param("param") QueryParam param);

}
