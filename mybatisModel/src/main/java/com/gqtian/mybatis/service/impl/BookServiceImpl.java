package com.gqtian.mybatis.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gqtian.mybatis.entity.Book;
import com.gqtian.mybatis.entity.params.QueryParam;
import com.gqtian.mybatis.entity.vo.BookVo;
import com.gqtian.mybatis.mapper.BookMapper;
import com.gqtian.mybatis.service.BookService;

/**
* @author gqtia
* @description 针对表【books(图书信息表)】的数据库操作Service实现
* @createDate 2025-12-23 15:47:20
*/
@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {

    @Override
    public Page<Book> pageLambda(Page<Book> page, String title) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(title != null, Book::getTitle, title).orderByDesc(Book::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public Page<Book> pageXml(Page<Book> page, QueryParam param) {
        return baseMapper.pageXml(page, param);
    }

    @Override
    public List<BookVo> nestedQueryXml(QueryParam param) {
        return baseMapper.nestedQueryXml(param);
    }

    @Override
    public List<BookVo> nestedResultXml(QueryParam param) {
        return baseMapper.nestedResultXml(param);
    }
}
