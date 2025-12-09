package com.gqtian.network.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gqtian.network.entity.SystemUser;
import com.gqtian.network.service.SystemUserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 🤨 用户控制类
 * @className TestService
 * @author gqtian
 * @date 2025/11/24 14:44
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class SystemUserController {

    private final SystemUserService systemUserService;

    /**
     * 查询用户信息（个体）
     */
    @RequestMapping("queryUserInfoObject")
    public SystemUser queryUserInfoObject() {
        return systemUserService.queryUserInfoObject();
    }

    /**
     * 查询用户信息集合
     */
    @RequestMapping("queryUserInfoPage")
    public List<SystemUser> queryUserInfoPage(Page<SystemUser> page) {
        return systemUserService.queryUserInfoPage(page);
    }
}
