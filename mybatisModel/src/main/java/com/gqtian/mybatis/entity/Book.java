package com.gqtian.mybatis.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 图书信息表
 * @TableName books
 */
@TableName(value = "book")
@Data
public class Book {
    /**
     * 图书ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * ISBN编号
     */
    private String isbn;

    /**
     * 书名
     */
    private String title;

    /**
     * 作者ID
     */
    private String authorId;

    /**
     * 出版社
     */
    private String publisher;

    /**
     * 出版日期
     */
    private Date publishDate;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 总库存数量
     */
    private Integer totalCopies;

    /**
     * 可借数量
     */
    private Integer availableCopies;

    /**
     * 馆藏位置
     */
    private String location;

    /**
     * 状态：1-可借，2-已借出，3-维修中，4-已下架
     */
    private Integer status;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 图书描述
     */
    private String description;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}