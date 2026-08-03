package com.gqtian.mongodb.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gqtian.mongodb.entity.UserDocument;
import com.gqtian.mongodb.entity.dto.UserQueryParam;
import com.gqtian.mongodb.service.UserMongoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * MongoDB 用户文档操作接口
 * <p>
 * 提供 CRUD、条件查询、分页、局部更新、标签数组操作、聚合统计等测试入口。
 * </p>
 *
 * @author gqtian
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserMongoController {

    private final UserMongoService userMongoService;

    // ---------- CRUD ----------

    /**
     * 新增用户
     */
    @PostMapping
    public UserDocument create(@RequestBody UserDocument user) {
        return userMongoService.create(user);
    }

    /**
     * 批量新增
     */
    @PostMapping("/batch")
    public List<UserDocument> createBatch(@RequestBody List<UserDocument> users) {
        return userMongoService.createBatch(users);
    }

    /**
     * 初始化演示数据（会清空 user 集合后重新写入）
     */
    @PostMapping("/initDemo")
    public List<UserDocument> initDemo() {
        return userMongoService.initDemoData();
    }

    /**
     * 按 ID 查询
     */
    @GetMapping("/{id}")
    public UserDocument getById(@PathVariable String id) {
        return userMongoService.getById(id);
    }

    /**
     * 按用户名查询
     */
    @GetMapping("/byUsername")
    public Optional<UserDocument> getByUsername(@RequestParam String username) {
        return userMongoService.getByUsername(username);
    }

    /**
     * 全量列表
     */
    @GetMapping("/list")
    public List<UserDocument> listAll() {
        return userMongoService.listAll();
    }

    /**
     * 文档总数
     */
    @GetMapping("/count")
    public long count() {
        return userMongoService.count();
    }

    /**
     * 整文档更新
     */
    @PutMapping("/{id}")
    public UserDocument update(@PathVariable String id, @RequestBody UserDocument user) {
        return userMongoService.update(id, user);
    }

    /**
     * 按 ID 删除
     */
    @DeleteMapping("/{id}")
    public String delete(@PathVariable String id) {
        userMongoService.deleteById(id);
        return "deleted";
    }

    // ---------- 查询 ----------

    /**
     * Repository 方法命名 / @Query 演示
     */
    @GetMapping("/repoDemo")
    public Map<String, Object> repoDemo(
        @RequestParam(required = false) Integer minAge,
        @RequestParam(required = false) Integer maxAge,
        @RequestParam(required = false) String tag) {
        return userMongoService.repositoryQueryDemo(minAge, maxAge, tag);
    }

    /**
     * 动态条件分页（MongoTemplate）
     */
    @PostMapping("/page")
    public Page<UserDocument> page(@RequestBody UserQueryParam param) {
        return userMongoService.pageQuery(param);
    }

    /**
     * 动态条件列表
     */
    @PostMapping("/search")
    public List<UserDocument> search(@RequestBody UserQueryParam param) {
        return userMongoService.listByCondition(param);
    }

    // ---------- 局部更新 / 数组 / 批量 ----------

    /**
     * 局部字段更新
     */
    @PutMapping("/{id}/partial")
    public long partialUpdate(
        @PathVariable String id,
        @RequestParam(required = false) String nickname,
        @RequestParam(required = false) Integer age,
        @RequestParam(required = false) Integer status) {
        return userMongoService.partialUpdate(id, nickname, age, status);
    }

    /**
     * 追加标签
     */
    @PostMapping("/{id}/tags")
    public long addTag(@PathVariable String id, @RequestParam String tag) {
        return userMongoService.addTag(id, tag);
    }

    /**
     * 移除标签
     */
    @DeleteMapping("/{id}/tags")
    public long removeTag(@PathVariable String id, @RequestParam String tag) {
        return userMongoService.removeTag(id, tag);
    }

    /**
     * 批量改状态
     */
    @PutMapping("/status/batch")
    public long batchUpdateStatus(
        @RequestParam Integer fromStatus,
        @RequestParam Integer toStatus) {
        return userMongoService.batchUpdateStatus(fromStatus, toStatus);
    }

    /**
     * 按状态条件删除
     */
    @DeleteMapping("/byStatus")
    public long deleteByStatus(@RequestParam Integer status) {
        return userMongoService.deleteByCondition(status);
    }

    // ---------- 聚合 ----------

    /**
     * 按城市聚合统计
     */
    @GetMapping("/aggregate/city")
    @SuppressWarnings("rawtypes")
    public List<Map> aggregateByCity() {
        return userMongoService.aggregateByCity();
    }
}
