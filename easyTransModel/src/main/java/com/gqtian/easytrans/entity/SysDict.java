package com.gqtian.easytrans.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 字典表（启动时加载到 DictionaryTransService）
 */
@Data
@TableName("sys_dict")
public class SysDict {

    @TableId(type = IdType.INPUT)
    private Long id;

    private String dictType;

    private String dictCode;

    private String dictLabel;

    @TableLogic
    private Integer deleted;
}
