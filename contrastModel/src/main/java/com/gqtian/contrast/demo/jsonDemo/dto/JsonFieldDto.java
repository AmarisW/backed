package com.gqtian.contrast.demo.jsonDemo.dto;

import com.alibaba.fastjson2.annotation.JSONField;

import lombok.Data;

/**
 * json测试Dto
 *
 * @author gqtian
 * @date 2023/03/10 13:53
 * @see com.web.entity.dto
 */
@Data
public class JsonFieldDto {
    @JSONField(name = "na")
    private String name;

    @JSONField(name = "un_test")
    private String unTest;

    @JSONField(name = "ag")
    private int age;

    @JSONField(name = "jo")
    private String jsonOne;
}
