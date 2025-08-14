package com.gqtian.fileStorage.config;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ConfigurationProperties示例
 * <p>
 * 注意一：@ConfigurationProperties注解的类，的属性，必须要有setter才行 注意二：@ConfigurationProperties注解的类，必须要注入Spring容器中
 * 注意三：如果是将配置信息写在yml文件中的话，那么最好直接写在application.yml或application.yaml 文件中，否者可能获取不到
 * <p>
 * 提示: 匹配时，会自动忽略大小写
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "yml.properties")
public class YmlProperties {
    
    private String normalString;
    
    private Integer normalNumber;
    
    private Boolean normalBoolean;
    
    /**
     * 这里类型采用数组或集合都可以
     */
    private String[] myListOrArray;
    
    private Map<String, Object> myMap;
    
    private YmlObject myUser;
    
    /**
     * 这里类型采用数组或集合都可以
     */
    private List<YmlObject> myListOrArrayPlus;
    
    private Map<String, YmlObject> myMapPlus;
    
    /**
     * ymlObject对象属性
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class YmlObject {
        
        /**
         * 身份证ID
         */
        private String cardId;
        
        /**
         * 姓名
         */
        private String name;
        
        /**
         * 年龄
         */
        private Integer age;
        
        /**
         * 性别
         */
        private String gender;
        
        /**
         * 座右铭
         */
        private String motto;
        
        /**
         * 帅气值
         */
        private Integer handsomeValue;
    }
    
}
