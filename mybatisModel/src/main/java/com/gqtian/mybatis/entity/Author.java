package com.gqtian.mybatis.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 作者信息表
 * @TableName authors
 */
@TableName(value = "author")
@Data
public class Author {
    /**
     * 作者ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 作者姓名
     */
    private String authorName;

    /**
     * 性别：0-女，1-男，2-其他
     */
    private Integer gender;

    /**
     * 国籍
     */
    private String country;

    /**
     * 出生日期
     */
    private Date birthDate;

    /**
     * 逝世日期
     */
    private Date deathDate;

    /**
     * 作者简介
     */
    private String biography;

    /**
     * 联系方式或官网
     */
    private String contactInfo;

    /**
     * 作者照片URL
     */
    private String authorPhoto;

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