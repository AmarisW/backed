package com.gqtian.easytrans.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import org.dromara.core.trans.vo.VO;

import lombok.Data;

/**
 * 用户表（SIMPLE 翻译的目标表，也可作为业务数据源）
 */
@Data
@TableName("sys_user")
public class SysUser implements VO {

    @TableId(type = IdType.INPUT)
    private Long id;

    private String username;

    private String realName;

    private Long deptId;

    /** 性别字典码，对应字典类型 gender */
    private String gender;

    /** 状态码，对应 UserStatusEnum */
    private Integer status;

    /** 创建人用户ID */
    private Long createBy;

    @TableLogic
    private Integer deleted;
}
