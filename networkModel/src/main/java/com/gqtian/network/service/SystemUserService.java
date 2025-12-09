package com.gqtian.network.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.repository.CrudRepository;
import com.gqtian.network.entity.SystemUser;
import com.gqtian.network.mapper.SystemUserMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 👻
 * 
 * @className UserService
 * @author gqtian
 * @date 2025/8/14 23:53
 */
@Slf4j
@Service
public class SystemUserService
    // extends ServiceImpl<UserInfoMapper, UserInfo> // 继承 ServiceImpl (旧版本)
    extends CrudRepository<SystemUserMapper, SystemUser> // 继承 CrudRepository (新版本推荐使用)
{

    /**
     * 查询用户信息（个体）
     * @return 用户信息
     */
    public SystemUser queryUserInfoObject() {
        LambdaQueryWrapper<SystemUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        return this.getOne(lambdaQueryWrapper);
    }

    /**
     * 查询用户信息（分页）
     */
    public List<SystemUser> queryUserInfoPage(Page<SystemUser> page) {
        LambdaQueryWrapper<SystemUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        return this.page(page, lambdaQueryWrapper).getRecords();
    }
}
