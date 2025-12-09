package com.gqtian.contrast.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gqtian.contrast.entity.SystemUser;
import com.gqtian.contrast.service.SystemUserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 🤨 系统用户控制层
 * @className SystemUserController
 * @author gqtian
 * @date 2025/12/2 16:41
 */
@Slf4j
@RestController
@RequestMapping("/system")
@RequiredArgsConstructor
public class SystemController {

    private final SystemUserRepository systemUserRepository;

    /**
     * 分页查询系统用户
     */
    @RequestMapping("/queryUserInfoPage")
    public Page<SystemUser> queryUserInfoPage(Page<SystemUser> page) {
        return systemUserRepository.queryUserInfoPage(page);
    }

}
