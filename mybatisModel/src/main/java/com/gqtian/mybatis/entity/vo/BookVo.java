package com.gqtian.mybatis.entity.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * 图书信息表
 * @TableName books
 */
@Data
public class BookVo {

    /**
     * 图书ID
     */
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
     * 作者
     */
    private Long authorId;

    /**
     * 出版社
     */
    private String publisher;

    /**
     * 出版日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
     * 作者信息
     */
    private AuthorVo authors;

    /**
     * 借阅记录表
     */
    private List<BorrowRecordVo> borrowRecords;

}