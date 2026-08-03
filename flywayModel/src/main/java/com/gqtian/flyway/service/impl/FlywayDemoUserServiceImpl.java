package com.gqtian.flyway.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gqtian.flyway.entity.FlywayDemoUser;
import com.gqtian.flyway.mapper.FlywayDemoUserMapper;
import com.gqtian.flyway.service.FlywayDemoUserService;

/**
 * 演示用户表 Service 实现
 *
 * @author gqtian
 */
@Service
public class FlywayDemoUserServiceImpl extends ServiceImpl<FlywayDemoUserMapper, FlywayDemoUser>
    implements FlywayDemoUserService {
}
