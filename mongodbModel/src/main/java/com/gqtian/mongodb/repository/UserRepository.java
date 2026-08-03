package com.gqtian.mongodb.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.gqtian.mongodb.entity.UserDocument;

/**
 * 用户文档 Repository（Spring Data MongoDB 方法命名 / 注解查询）
 *
 * @author gqtian
 */
public interface UserRepository extends MongoRepository<UserDocument, String> {

    /**
     * 按用户名精确查询
     */
    Optional<UserDocument> findByUsername(String username);

    /**
     * 按状态查询
     */
    List<UserDocument> findByStatus(Integer status);

    /**
     * 年龄区间查询
     */
    List<UserDocument> findByAgeBetween(Integer minAge, Integer maxAge);

    /**
     * 用户名模糊 + 分页
     */
    Page<UserDocument> findByUsernameContaining(String username, Pageable pageable);

    /**
     * 标签包含
     */
    List<UserDocument> findByTagsContaining(String tag);

    /**
     * JSON 查询：状态启用且年龄大于等于指定值
     */
    @Query("{ 'status': 1, 'age': { $gte: ?0 } }")
    List<UserDocument> findActiveUsersAgeGte(Integer minAge);

    /**
     * 是否存在该用户名
     */
    boolean existsByUsername(String username);
}
