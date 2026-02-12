package com.gqtian.common.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 全局API接口信息实体
 * <p>
 * 用于存储和传输API接口的详细信息，包括请求方法、路径、参数和描述等。
 * 主要用于接口文档生成和启动时接口信息展示
 * </p>
 * 
 * @author gqtian
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalApiOutEntity {
    /**
     * 请求方式（GET/POST/PUT/DELETE/PATCH等）
     */
    private String httpMethod;

    /**
     * 请求路径（完整URL路径）
     */
    private String path;

    /**
     * 请求参数列表
    
     */
    private List<ParamEntity> params;

    /**
     * 接口功能描述
     */
    private String comment;

    /**
     * API参数信息实体
     * <p>用于封装接口参数的名称和类型信息</p>
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParamEntity {

        /**
         * 参数名称（对于简单类型）或JSON示例（对于复杂对象）
         */
        private String name;

        /**
         * 参数类型（如String、Integer、自定义对象名等）
         */
        private String type;
    }
}