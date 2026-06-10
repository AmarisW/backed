package com.gqtian.easytrans.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gqtian.easytrans.entity.SysUser;
import com.gqtian.easytrans.entity.vo.SysUserVo;
import com.gqtian.easytrans.enums.UserStatusEnum;
import com.gqtian.easytrans.mapper.SysUserMapper;
import com.gqtian.easytrans.service.SysUserTransService;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;

/**
 * 用户翻译演示服务实现
 */
@Service
@RequiredArgsConstructor
public class SysUserTransServiceImpl implements SysUserTransService {

    private final SysUserMapper sysUserMapper;

    @Override
    public List<SysUserVo> listUsers() {
        return sysUserMapper.selectList(Wrappers.<SysUser>lambdaQuery().orderByAsc(SysUser::getId)).stream()
            .map(this::toVo)
            .toList();
    }

    @Override
    public SysUserVo getUserById(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        return user == null ? null : toVo(user);
    }

    private SysUserVo toVo(SysUser user) {
        SysUserVo vo = BeanUtil.copyProperties(user, SysUserVo.class);
        vo.setStatus(UserStatusEnum.of(user.getStatus()));
        return vo;
    }
}
