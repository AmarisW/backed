package com.gqtian.common.model;

import java.util.List;

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
    private List<ParamEntity> params;
    
    /**
     * 接口描述
     */
    private String comment;
    
    /**
     * 内部类参数对象
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParamEntity {
        
        /**
         * 参数名称
         */
        private String name;
        
        /**
         * 参数类型
         */
        private String type;
    }
}