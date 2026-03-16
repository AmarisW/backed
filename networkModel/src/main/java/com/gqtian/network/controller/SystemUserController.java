package com.gqtian.network.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gqtian.common.annotation.NoGlobalWrap;
import com.gqtian.network.entity.CollectResult;
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
    @NoGlobalWrap
    @RequestMapping("queryUserInfoObject")
    public CollectResult<SystemUser> queryUserInfoObject() {
        SystemUser systemUser = systemUserService.queryUserInfoObject();
        CollectResult<SystemUser> result = new CollectResult<>();
        result.setStatus(200);
        result.setErrorCode("");
        result.setErrorMsg("");
        result.setData(systemUser);
        return result;
    }

    /**
     * 查询用户信息分页
     */
    @NoGlobalWrap
    @RequestMapping("queryUserInfoPage")
    public CollectResult<List<SystemUser>> queryUserInfoPage(String page, String pageSize) {
        Page<SystemUser> userPage = new Page<>(Long.parseLong(page), Long.parseLong(pageSize));
        List<SystemUser> systemUsers = systemUserService.queryUserInfoPage(userPage);
        CollectResult<List<SystemUser>> result = new CollectResult<>();
        result.setStatus(200);
        result.setErrorCode("");
        result.setErrorMsg("");
        result.setData(systemUsers);
        return result;
    }

    /**
     * 生成集合方法
     */
    @NoGlobalWrap
    @RequestMapping("queryUserInfoList")
    public CollectResult<List<SystemUser>> queryUserInfoList() {
        List<SystemUser> systemUsers = systemUserService.list();
        CollectResult<List<SystemUser>> result = new CollectResult<>();
        result.setStatus(200);
        result.setErrorCode("");
        result.setErrorMsg("");
        result.setData(systemUsers);
        return result;
    }
}
