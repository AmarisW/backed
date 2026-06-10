package com.gqtian.easytrans.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.gqtian.easytrans.entity.vo.SysUserVo;

/**
 * 翻译效果验证：需本地 MySQL 已执行 schema.sql
 */
@SpringBootTest
class SysUserTransServiceTest {

    @Autowired
    private SysUserTransService sysUserTransService;

    @Test
    void listUsersShouldReturnData() {
        assertThat(sysUserTransService.listUsers()).isNotEmpty();
    }

    @Test
    void getUserByIdShouldReturnUser() {
        SysUserVo user = sysUserTransService.getUserById(2L);
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("zhangsan");
    }
}
