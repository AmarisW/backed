package com.gqtian.common.model;

import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;

/**
 * 🔔 系统字典
 * 
 * @author GQTIAN
 * @date 2025/7/24 17:23
 **/
@Data
public class GlobalDict {

    /**
     * 主键 ID
     */
    @TableId
    private String id;

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
