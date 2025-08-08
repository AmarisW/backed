package com.gqtian.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 全局接口返回参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalApiOutEntity {
    /**
     * 请求方式
     */
    private String httpMethod;
    
    /**
     * 请求路径
     */
    private String path;
    
    /**
     * 请求参数
     */
    private String params;
    
    /**
     * 接口描述
     */
    private String comment;
    
}