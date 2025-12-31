package com.gqtian.mybatis.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gqtian.mybatis.entity.Books;
import com.gqtian.mybatis.entity.params.QueryParam;
import com.gqtian.mybatis.service.BooksService;

import lombok.RequiredArgsConstructor;

/**
 * 图书管理Controller
 * 
 * @author gqtian
 */
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BooksController {

    private final BooksService booksService;

    /**
     * 分页查询图书列表（条件构造器）
     *
     * @param title 书名(可选)
     * @return 图书分页数据
     */
    @GetMapping("/pageLambda")
    public Page<Books> pageLambda(Page<Books> page, @RequestParam(required = false) String title) {
        return booksService.pageLambda(page, title);
    }

    /**
     * 分页查询图书列表（xml文件）
     *
     * @param page 分页信息
     * @param param 查询参数
     * @return 图书分页数据
     */
    @PostMapping("/pageXml")
    public Page<Books> pageXml(Page<Books> page, @RequestBody QueryParam param) {
        return booksService.pageXml(page, param);
    }

}
