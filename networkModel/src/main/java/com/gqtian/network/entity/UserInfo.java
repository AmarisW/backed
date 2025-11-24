package com.gqtian.network.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 🤨 测试对象
 * @className Test
 * @author gqtian
 * @date 2025/11/24 14:44
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

    /**
     * 名称
     */
    private String name;

    /**
     * 年龄
     */
    private Integer age;
}
