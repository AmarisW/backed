package com.gqtian.contrast.mapper;

import org.mybatis.spring.annotation.MapperScan;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gqtian.contrast.entity.SystemUser;

/**
 * 😶‍🌫️ 用户
 * 
 * @className UserMapper
 * @author gqtian
 * @date 2025/8/14 23:59
 */
@MapperScan
public interface SystemUserMapper extends BaseMapper<SystemUser> {

}
