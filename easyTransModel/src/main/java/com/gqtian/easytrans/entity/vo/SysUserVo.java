package com.gqtian.easytrans.entity.vo;

import org.dromara.core.trans.anno.Trans;
import org.dromara.core.trans.constant.TransType;
import org.dromara.core.trans.vo.TransPojo;
import com.gqtian.easytrans.constant.EasyTransConstant;
import com.gqtian.easytrans.entity.SysDept;
import com.gqtian.easytrans.entity.SysUser;
import com.gqtian.easytrans.enums.UserStatusEnum;

import lombok.Data;

/**
 * 用户展示对象，演示 Easy-Trans 三种翻译方式
 *
 * <ul>
 *   <li>DICTIONARY：gender -> genderName</li>
 *   <li>SIMPLE：deptId -> deptName，createBy -> createByName</li>
 *   <li>ENUM：status -> statusName</li>
 * </ul>
 */
@Data
public class SysUserVo implements TransPojo {

    private Long id;

    private String username;

    private String realName;

    /** 字典翻译：gender 字典码 -> genderName */
    @Trans(type = TransType.DICTIONARY, key = EasyTransConstant.DICT_GENDER, ref = "genderName")
    private String gender;

    private String genderName;

    /** 简单翻译：部门 ID -> 部门名称 */
    @Trans(type = TransType.SIMPLE, target = SysDept.class, fields = "deptName", ref = "deptName")
    private Long deptId;

    private String deptName;

    /** 简单翻译：创建人 ID -> 真实姓名 */
    @Trans(type = TransType.SIMPLE, target = SysUser.class, fields = "realName", ref = "createByName")
    private Long createBy;

    private String createByName;

    /** 枚举翻译：状态枚举 -> 中文标签 */
    @Trans(type = TransType.ENUM, key = "label", ref = "statusName")
    private UserStatusEnum status;

    private String statusName;
}
