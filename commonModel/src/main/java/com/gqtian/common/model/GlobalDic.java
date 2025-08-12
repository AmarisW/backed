package com.gqtian.common.model;

import lombok.Data;

/**
 * 🔔 系统字典
 * 
 * @author GQTIAN
 * @date 2025/7/24 17:23
 **/
@Data
public class GlobalDic {
    
    /**
     * 编码
     */
    private String code;
    
    /**
     * 值
     */
    private String value;
    
    /**
     * 所属组
     */
    private String type;
    
}
