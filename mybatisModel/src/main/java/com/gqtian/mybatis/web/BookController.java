package com.gqtian.mybatis.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gqtian.mybatis.entity.Book;
import com.gqtian.mybatis.entity.BookSlave;
import com.gqtian.mybatis.entity.params.QueryParam;
import com.gqtian.mybatis.entity.vo.BookVo;
import com.gqtian.mybatis.service.BookService;
import com.gqtian.mybatis.service.BookSlaveService;

import lombok.RequiredArgsConstructor;

/**
 * 图书管理Controller
 * 
 * @author gqtian
 */
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final BookSlaveService bookSlaveService;

    /**
     * 分页查询图书列表（条件构造器）
     *
     * @param title 书名(可选)
     * @return 图书分页数据
     */
    @GetMapping("/pageLambda")
    public Page<Book> pageLambda(Page<Book> page, @RequestParam(required = false) String title) {
        return bookService.pageLambda(page, title);
    }

    /**
     * 从库分页查询图书列表（条件构造器）
     * @param page 分页信息
     * @param title 书名(可选)
     * @return 图书分页数据
     */
    @GetMapping("/pageSlave")
    public Page<BookSlave> pageSlave(Page<BookSlave> page, @RequestParam(required = false) String title) {
        return bookSlaveService.pageLambdaSlave(page, title);
    }

    /**
     * 分页查询图书列表（xml文件）
     *
     * @param page 分页信息
     * @param param 查询参数
     * @return 图书分页数据
     */
    @PostMapping("/pageXml")
    public Page<Book> pageXml(Page<Book> page, @RequestBody QueryParam param) {
        return bookService.pageXml(page, param);
    }

    /**
     * 嵌套查询
     * @param param 查询参数
     * @return 图书列表
     */
    @PostMapping("/nestedQueryXml")
    public List<BookVo> nestedQueryXml(@RequestBody QueryParam param) {
        return bookService.nestedQueryXml(param);
    }

    /**
     * 嵌套结果查询
     * @param param 查询参数
     * @return 图书列表
     */
    @PostMapping("/nestedResultXml")
    public List<BookVo> nestedResultXml(@RequestBody QueryParam param) {
        return bookService.nestedResultXml(param);
    }

}
