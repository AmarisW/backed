package com.gqtian.contrast.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.repository.CrudRepository;
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
public class SystemUserRepository
    // extends ServiceImpl<SystemUserRepositoryMapper, SystemUserRepository> // 继承 ServiceImpl (旧版本)
    extends CrudRepository<SystemUserMapper, SystemUser> // 继承 CrudRepository (新版本推荐使用)
    implements PageQuerySupplier<SystemUser> {

    @Override
    public List<SystemUser> getPage(int pageNum, int pageSize) {
        return this.list(new Page<>(pageNum, pageSize));
    }

    @Override
    public Long getTotal() {
        return this.count();
    }

    public Page<SystemUser> queryUserInfoPage(Page<SystemUser> page) {
        return this.page(page);
    }
}
