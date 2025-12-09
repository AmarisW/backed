package com.gqtian.network.service;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.repository.CrudRepository;
import com.gqtian.network.entity.UserInfo;
import com.gqtian.network.mapper.UserInfoMapper;

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
public class UserInfoRepository
    // extends ServiceImpl<UserInfoMapper, UserInfo> // 继承 ServiceImpl (旧版本)
    extends CrudRepository<UserInfoMapper, UserInfo> // 继承 CrudRepository (新版本推荐使用)
{

}
