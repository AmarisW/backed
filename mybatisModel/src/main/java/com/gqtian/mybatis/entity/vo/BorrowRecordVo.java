
package com.gqtian.mybatis.entity.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * 借阅记录表
 * @TableName borrow_record
 */
@Data
public class BorrowRecordVo {
    /**
     * 记录ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 图书ID
     */
    private Integer bookId;

    /**
     * 借阅时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date borrowTime;

    /**
     * 应还时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expectedReturnTime;

    /**
     * 实际归还时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date actualReturnTime;

    /**
     * 续借次数
     */
    private Integer renewCount;

}