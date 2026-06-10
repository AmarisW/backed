package com.gqtian.easytrans.example.vo;

import org.dromara.core.trans.anno.Trans;
import org.dromara.core.trans.constant.TransType;
import org.dromara.core.trans.vo.TransPojo;
import com.gqtian.easytrans.entity.SysDept;
import com.gqtian.easytrans.entity.SysUser;

import lombok.Data;

/**
 * SIMPLE 简单翻译示例
 *
 * <p>框架通过 MyBatis-Plus 自动查表，将 ID 翻译为目标字段，无需手写 JOIN。</p>
 * <p>需要数据库中存在 sys_dept、sys_user 表及对应数据。</p>
 */
@Data
public class SimpleExampleVo implements TransPojo {

    private String scene = "SIMPLE 简单翻译（自动查表）";

    /** 部门 ID -> sys_dept.dept_name */
    @Trans(type = TransType.SIMPLE, target = SysDept.class, fields = "deptName", ref = "deptName")
    private Long deptId;

    private String deptName;

    /** 用户 ID -> sys_user.real_name */
    @Trans(type = TransType.SIMPLE, target = SysUser.class, fields = "realName", ref = "leaderName")
    private Long leaderId;

    private String leaderName;
}
