package com.gqtian.easytrans.service;

import java.util.List;

import com.gqtian.easytrans.entity.vo.SysUserVo;

/**
 * 用户翻译演示服务
 */
public interface SysUserTransService {

    /**
     * 查询全部用户（返回带 @Trans 注解的 VO，由全局拦截器自动翻译）
     */
    List<SysUserVo> listUsers();

    /**
     * 按 ID 查询用户
     */
    SysUserVo getUserById(Long id);
}
