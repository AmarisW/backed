package com.gqtian.easytrans.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gqtian.easytrans.entity.vo.SysUserVo;
import com.gqtian.easytrans.service.SysUserTransService;

import lombok.RequiredArgsConstructor;

/**
 * 用户翻译演示接口
 *
 * <p>启动后访问：</p>
 * <ul>
 *   <li>GET /easyTransModel/users — 用户列表（自动翻译）</li>
 *   <li>GET /easyTransModel/users/{id} — 单个用户（自动翻译）</li>
 * </ul>
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserTransService sysUserTransService;

    @GetMapping
    public List<SysUserVo> listUsers() {
        return sysUserTransService.listUsers();
    }

    @GetMapping("/{id}")
    public SysUserVo getUser(@PathVariable Long id) {
        return sysUserTransService.getUserById(id);
    }
}
