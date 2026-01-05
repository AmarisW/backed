package com.gqtian.mybatis.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gqtian.mybatis.entity.Author;
import com.gqtian.mybatis.mapper.AuthorMapper;
import com.gqtian.mybatis.service.AuthorService;

/**
* @author gqtia
* @description 针对表【authors(作者信息表)】的数据库操作Service实现
* @createDate 2026-01-05 15:01:10
*/
@Service
public class AuthorServiceImpl extends ServiceImpl<AuthorMapper, Author> implements AuthorService {

}
