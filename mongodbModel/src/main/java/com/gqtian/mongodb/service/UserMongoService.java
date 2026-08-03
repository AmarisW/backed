package com.gqtian.mongodb.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gqtian.common.globalProcess.GlobalException;
import com.gqtian.mongodb.entity.UserDocument;
import com.gqtian.mongodb.entity.dto.UserQueryParam;
import com.gqtian.mongodb.repository.UserRepository;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * MongoDB 用户文档操作服务
 * <p>
 * 同时演示 {@link UserRepository} 与 {@link MongoTemplate} 两种访问方式。
 * </p>
 *
 * @author gqtian
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserMongoService {

    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;

    // ========================= 基础 CRUD（Repository） =========================

    /**
     * 新增用户
     */
    public UserDocument create(UserDocument user) {
        if (!StringUtils.hasText(user.getUsername())) {
            throw new GlobalException("用户名不能为空");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new GlobalException("用户名已存在: " + user.getUsername());
        }
        LocalDateTime now = LocalDateTime.now();
        user.setId(null);
        user.setCreateTime(now);
        user.setUpdateTime(now);
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        UserDocument saved = userRepository.save(user);
        log.info("新增用户成功, id={}, username={}", saved.getId(), saved.getUsername());
        return saved;
    }

    /**
     * 批量新增
     */
    public List<UserDocument> createBatch(List<UserDocument> users) {
        LocalDateTime now = LocalDateTime.now();
        for (UserDocument user : users) {
            if (userRepository.existsByUsername(user.getUsername())) {
                throw new GlobalException("用户名已存在: " + user.getUsername());
            }
            user.setId(null);
            user.setCreateTime(now);
            user.setUpdateTime(now);
            if (user.getStatus() == null) {
                user.setStatus(1);
            }
        }
        return userRepository.saveAll(users);
    }

    /**
     * 按 ID 查询
     */
    public UserDocument getById(String id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new GlobalException("用户不存在, id=" + id));
    }

    /**
     * 按用户名查询
     */
    public Optional<UserDocument> getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 全量列表
     */
    public List<UserDocument> listAll() {
        return userRepository.findAll(Sort.by(Sort.Direction.DESC, "createTime"));
    }

    /**
     * 更新（整文档覆盖式 save，保留 createTime）
     */
    public UserDocument update(String id, UserDocument user) {
        UserDocument exist = getById(id);
        user.setId(id);
        user.setCreateTime(exist.getCreateTime());
        user.setUpdateTime(LocalDateTime.now());
        if (!StringUtils.hasText(user.getUsername())) {
            user.setUsername(exist.getUsername());
        } else if (!user.getUsername().equals(exist.getUsername())
            && userRepository.existsByUsername(user.getUsername())) {
            throw new GlobalException("用户名已存在: " + user.getUsername());
        }
        return userRepository.save(user);
    }

    /**
     * 按 ID 删除
     */
    public void deleteById(String id) {
        if (!userRepository.existsById(id)) {
            throw new GlobalException("用户不存在, id=" + id);
        }
        userRepository.deleteById(id);
        log.info("删除用户成功, id={}", id);
    }

    // ========================= 条件查询（Repository + Template） =========================

    /**
     * Repository 方法命名查询示例
     */
    public Map<String, Object> repositoryQueryDemo(Integer minAge, Integer maxAge, String tag) {
        Map<String, Object> result = new HashMap<>(8);
        result.put("byStatusEnabled", userRepository.findByStatus(1));
        if (minAge != null && maxAge != null) {
            result.put("byAgeBetween", userRepository.findByAgeBetween(minAge, maxAge));
        }
        if (StringUtils.hasText(tag)) {
            result.put("byTag", userRepository.findByTagsContaining(tag));
        }
        if (minAge != null) {
            result.put("activeAgeGte", userRepository.findActiveUsersAgeGte(minAge));
        }
        return result;
    }

    /**
     * 动态条件分页查询（MongoTemplate）
     */
    public Page<UserDocument> pageQuery(UserQueryParam param) {
        int page = param.getPage() == null ? 0 : Math.max(param.getPage(), 0);
        int size = param.getSize() == null ? 10 : Math.max(param.getSize(), 1);

        Query query = buildQuery(param);
        long total = mongoTemplate.count(query, UserDocument.class);

        query.with(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "create_time")));
        List<UserDocument> list = mongoTemplate.find(query, UserDocument.class);

        return new org.springframework.data.domain.PageImpl<>(list, PageRequest.of(page, size), total);
    }

    /**
     * 条件列表（不分页）
     */
    public List<UserDocument> listByCondition(UserQueryParam param) {
        return mongoTemplate.find(buildQuery(param), UserDocument.class);
    }

    private Query buildQuery(UserQueryParam param) {
        Criteria criteria = new Criteria();
        if (StringUtils.hasText(param.getUsername())) {
            criteria.and("username").regex(param.getUsername(), "i");
        }
        if (param.getMinAge() != null && param.getMaxAge() != null) {
            criteria.and("age").gte(param.getMinAge()).lte(param.getMaxAge());
        } else if (param.getMinAge() != null) {
            criteria.and("age").gte(param.getMinAge());
        } else if (param.getMaxAge() != null) {
            criteria.and("age").lte(param.getMaxAge());
        }
        if (param.getStatus() != null) {
            criteria.and("status").is(param.getStatus());
        }
        if (StringUtils.hasText(param.getTag())) {
            criteria.and("tags").in(param.getTag());
        }
        if (StringUtils.hasText(param.getCity())) {
            criteria.and("profile.city").is(param.getCity());
        }
        return new Query(criteria);
    }

    // ========================= MongoTemplate 局部更新 / 删除 =========================

    /**
     * 局部字段更新（$set）
     */
    public long partialUpdate(String id, String nickname, Integer age, Integer status) {
        Query query = Query.query(Criteria.where("_id").is(id));
        Update update = new Update().set("update_time", LocalDateTime.now());
        if (StringUtils.hasText(nickname)) {
            update.set("nickname", nickname);
        }
        if (age != null) {
            update.set("age", age);
        }
        if (status != null) {
            update.set("status", status);
        }
        UpdateResult result = mongoTemplate.updateFirst(query, update, UserDocument.class);
        log.info("局部更新 id={}, matched={}, modified={}", id, result.getMatchedCount(), result.getModifiedCount());
        return result.getModifiedCount();
    }

    /**
     * 向 tags 追加标签（$addToSet）
     */
    public long addTag(String id, String tag) {
        if (!StringUtils.hasText(tag)) {
            throw new GlobalException("标签不能为空");
        }
        Query query = Query.query(Criteria.where("_id").is(id));
        Update update = new Update().addToSet("tags", tag).set("update_time", LocalDateTime.now());
        UpdateResult result = mongoTemplate.updateFirst(query, update, UserDocument.class);
        return result.getModifiedCount();
    }

    /**
     * 从 tags 移除标签（$pull）
     */
    public long removeTag(String id, String tag) {
        Query query = Query.query(Criteria.where("_id").is(id));
        Update update = new Update().pull("tags", tag).set("update_time", LocalDateTime.now());
        UpdateResult result = mongoTemplate.updateFirst(query, update, UserDocument.class);
        return result.getModifiedCount();
    }

    /**
     * 按条件批量更新状态
     */
    public long batchUpdateStatus(Integer fromStatus, Integer toStatus) {
        Query query = Query.query(Criteria.where("status").is(fromStatus));
        Update update = new Update().set("status", toStatus).set("update_time", LocalDateTime.now());
        UpdateResult result = mongoTemplate.updateMulti(query, update, UserDocument.class);
        log.info("批量更新状态 {} -> {}, modified={}", fromStatus, toStatus, result.getModifiedCount());
        return result.getModifiedCount();
    }

    /**
     * 按条件删除
     */
    public long deleteByCondition(Integer status) {
        Query query = Query.query(Criteria.where("status").is(status));
        DeleteResult result = mongoTemplate.remove(query, UserDocument.class);
        return result.getDeletedCount();
    }

    // ========================= 聚合统计 =========================

    /**
     * 按城市分组统计用户数与平均年龄
     */
    @SuppressWarnings("rawtypes")
    public List<Map> aggregateByCity() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("profile.city").exists(true).ne("")),
            Aggregation.group("profile.city")
                .count().as("userCount")
                .avg("age").as("avgAge")
                .max("age").as("maxAge")
                .min("age").as("minAge"),
            Aggregation.project("userCount", "avgAge", "maxAge", "minAge")
                .and("_id").as("city")
                .andExclude("_id"),
            Aggregation.sort(Sort.Direction.DESC, "userCount")
        );
        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, "user", Map.class);
        return results.getMappedResults();
    }

    /**
     * 文档总数
     */
    public long count() {
        return userRepository.count();
    }

    /**
     * 初始化演示数据（幂等：先清空集合再写入）
     */
    public List<UserDocument> initDemoData() {
        mongoTemplate.dropCollection(UserDocument.class);
        List<UserDocument> demo = List.of(
            UserDocument.builder()
                .username("alice")
                .nickname("爱丽丝")
                .age(22)
                .email("alice@example.com")
                .status(1)
                .tags(List.of("java", "mongo"))
                .profile(UserDocument.Profile.builder().city("上海").bio("后端开发").build())
                .build(),
            UserDocument.builder()
                .username("bob")
                .nickname("鲍勃")
                .age(28)
                .email("bob@example.com")
                .status(1)
                .tags(List.of("python", "mongo"))
                .profile(UserDocument.Profile.builder().city("北京").bio("数据工程师").build())
                .build(),
            UserDocument.builder()
                .username("cindy")
                .nickname("辛迪")
                .age(35)
                .email("cindy@example.com")
                .status(0)
                .tags(List.of("go"))
                .profile(UserDocument.Profile.builder().city("上海").bio("运维").build())
                .build(),
            UserDocument.builder()
                .username("david")
                .nickname("大卫")
                .age(30)
                .email("david@example.com")
                .status(1)
                .tags(List.of("java", "redis"))
                .profile(UserDocument.Profile.builder().city("深圳").bio("架构师").build())
                .build()
        );
        return createBatch(demo);
    }
}
