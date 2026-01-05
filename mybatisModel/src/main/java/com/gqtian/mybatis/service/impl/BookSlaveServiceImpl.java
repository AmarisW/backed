package com.gqtian.mybatis.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gqtian.mybatis.constant.SystemConstant.DataSourceType;
import com.gqtian.mybatis.entity.BookSlave;
import com.gqtian.mybatis.mapper.BookSlaveMapper;
import com.gqtian.mybatis.service.BookSlaveService;

/**
* @author gqtia
* @description 针对表【books(图书信息表)】的数据库操作Service实现
* @createDate 2025-12-23 15:47:20
*/
@Service
@DS(DataSourceType.SLAVE) // 使用从库
public class BookSlaveServiceImpl extends ServiceImpl<BookSlaveMapper, BookSlave>
    implements BookSlaveService {

    /**
     * 分页查询图书列表（条件构造器）
     *
     * @param page 分页信息
     * @param title 书名(可选)
     * @return 图书分页数据
     */
    @Override
    public Page<BookSlave> pageLambdaSlave(Page<BookSlave> page, String title) {
        LambdaQueryWrapper<BookSlave> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(title), BookSlave::getTitle, title)
            .orderByDesc(BookSlave::getCreateTime);
        return this.page(page, wrapper);
    }
}
