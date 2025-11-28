package com.gqtian.contrast.demo.JSON注解.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author gqtian
 * @date 2023/04/20 17:08
 * @see com.web.entity.dto
 */
@Data
public class JsonFieldAndJsonPropertyDto {

    // 姓名
    @JsonProperty(value = "na")
    @JSONField(name = "na")
    private String name;

    // 年龄
    @JsonProperty(value = "ag")
    @JSONField(name = "ag")
    private Integer age;

    // 未知
    @JsonProperty(value = "un_test")
    @JSONField(name = "un_test")
    private String unTest;

}
