package com.gqtian.mybatis.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gqtian.mybatis.entity.Books;
import com.gqtian.mybatis.entity.params.QueryParam;
import com.gqtian.mybatis.mapper.BooksMapper;
import com.gqtian.mybatis.service.BooksService;

/**
* @author gqtia
* @description 针对表【books(图书信息表)】的数据库操作Service实现
* @createDate 2025-12-23 15:47:20
*/
@Service
public class BooksServiceImpl extends ServiceImpl<BooksMapper, Books> implements BooksService {

    @Override
    public Page<Books> pageLambda(Page<Books> page, String title) {
        LambdaQueryWrapper<Books> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(title != null, Books::getTitle, title).orderByDesc(Books::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public Page<Books> pageXml(Page<Books> page, QueryParam param) {
        return baseMapper.pageXml(page, param);
    }
}
