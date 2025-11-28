package com.gqtian.contrast.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gqtian.contrast.demo.easyExcel.PageQuerySupplier;
import com.gqtian.contrast.entity.SystemUser;
import com.gqtian.contrast.mapper.SystemUserMapper;

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
public class SystemUserService extends ServiceImpl<SystemUserMapper, SystemUser>
    implements PageQuerySupplier<SystemUser> {

    @Override
    public List<SystemUser> getPage(int pageNum, int pageSize) {
        return this.list(new Page<>(pageNum, pageSize));
    }

    @Override
    public Long getTotal() {
        return this.count();
    }
}
