package com.gqtian.contrast.demo.JSON注解.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author gqtian
 * @date 2023/04/20 11:06
 * @see com.web.entity.dto
 */

@Data
public class JsonPropertyDto {
    // 姓名
    @JsonProperty(value = "na")
    private String name;

    // 年龄
    @JsonProperty(value = "ag")
    private Integer age;

    // 未知
    @JsonProperty(value = "un_test")
    private String unTest;

}
