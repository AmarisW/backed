package com.gqtian.mybatis.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 借阅记录表
 * @TableName borrow_records
 */
@TableName(value ="borrow_records")
@Data
public class BorrowRecords {
    /**
     * 记录ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long recordId;

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
    private Date borrowTime;

    /**
     * 应还时间
     */
    private Date expectedReturnTime;

    /**
     * 实际归还时间
     */
    private Date actualReturnTime;

    /**
     * 续借次数
     */
    private Integer renewCount;

    /**
     * 状态：1-借阅中，2-已归还，3-逾期，4-丢失
     */
    private Integer status;

    /**
     * 操作员ID（管理员ID）
     */
    private Integer operatorId;

    /**
     * 备注
     */
    private String notes;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

}