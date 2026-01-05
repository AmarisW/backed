package com.gqtian.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gqtian.mybatis.entity.Book;
import com.gqtian.mybatis.entity.params.QueryParam;
import com.gqtian.mybatis.entity.vo.BookVo;

/**
* @author gqtia
* @description 针对表【books(图书信息表)】的数据库操作Mapper
* @createDate 2025-12-23 15:47:20
* @Entity com.gqtian.mybatis.entity.Book
*/
public interface BookMapper extends BaseMapper<Book> {

    /**
     * 使用 XML 自定义 SQL 进行分页查询
     * 
     * @param page 分页对象
     * @param param 参选参数
     * @return 分页结果
     */
    Page<Book> pageXml(@Param("page") Page<Book> page, @Param("param") QueryParam param);

    /**
     * 嵌套结果查询
     * 
     * @param param 参选参数
     * @return 图书列表
     */
    List<BookVo> nestedQueryXml(@Param("param") QueryParam param);

    /**
     * 嵌套结果查询
     *
     * @param param 参选参数
     * @return 图书列表
     */
    List<BookVo> nestedResultXml(@Param("param") QueryParam param);
}
