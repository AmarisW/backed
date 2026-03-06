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
        result.setErrorCode("RC100");
        result.setErrorMsg("错误信息");
        result.setData(systemUser);
        return result;
    }

    /**
     * 查询用户信息集合
     */
    @NoGlobalWrap
    @RequestMapping("queryUserInfoPage")
    public CollectResult<List<SystemUser>> queryUserInfoPage(Page<SystemUser> page) {
        List<SystemUser> systemUsers = systemUserService.queryUserInfoPage(page);
        CollectResult<List<SystemUser>> result = new CollectResult<>();
        result.setStatus(200);
        result.setErrorCode("RC100");
        result.setErrorMsg("错误信息");
        result.setData(systemUsers);
        return result;
    }
}
