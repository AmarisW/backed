package com.gqtian.mybatis.entity.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * 作者信息表
 * @TableName authors
 */
@Data
public class AuthorVo {
    /**
     * 作者ID
     */
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date birthDate;

    /**
     * 逝世日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deathDate;

    /**
     * 作者简介
     */
    private String biography;

    /**
     * 作者照片URL
     */
    private String authorPhoto;

}