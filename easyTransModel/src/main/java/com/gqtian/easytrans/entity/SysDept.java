package com.gqtian.easytrans.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import org.dromara.core.trans.vo.VO;

import lombok.Data;

/**
 * 部门表（SIMPLE 翻译的目标表）
 */
@Data
@TableName("sys_dept")
public class SysDept implements VO {

    @TableId(type = IdType.INPUT)
    private Long id;

    private String deptName;

    @TableLogic
    private Integer deleted;
}
