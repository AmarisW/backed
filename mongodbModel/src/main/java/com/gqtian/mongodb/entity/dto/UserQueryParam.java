package com.gqtian.mongodb.entity.dto;

import lombok.Data;

/**
 * 用户条件查询参数
 *
 * @author gqtian
 */
@Data
public class UserQueryParam {

    /** 用户名（模糊） */
    private String username;

    /** 最小年龄（含） */
    private Integer minAge;

    /** 最大年龄（含） */
    private Integer maxAge;

    /** 状态 */
    private Integer status;

    /** 标签（包含） */
    private String tag;

    /** 城市（嵌套字段） */
    private String city;

    /** 页码，从 0 开始 */
    private Integer page = 0;

    /** 每页条数 */
    private Integer size = 10;
}
