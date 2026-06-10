package com.gqtian.easytrans.example.auto;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.dromara.core.trans.anno.AutoTrans;
import org.dromara.trans.service.AutoTransable;
import org.springframework.stereotype.Service;

import com.gqtian.easytrans.constant.EasyTransConstant;

/**
 * AUTO_TRANS 自定义数据源：内存讲师数据
 *
 * <p>演示不依赖数据库，自行实现 {@link AutoTransable#selectById} 提供翻译数据。</p>
 */
@Service
@AutoTrans(namespace = EasyTransConstant.AUTO_TEACHER, fields = {"name", "title"}, defaultAlias = "teacher")
public class TeacherAutoTransProvider implements AutoTransable<Teacher> {

    private static final Map<Long, Teacher> TEACHERS = Map.of(
        101L, new Teacher(101L, "王老师", "高级讲师"),
        102L, new Teacher(102L, "李老师", "特级教师"),
        103L, new Teacher(103L, "张老师", "金牌讲师"));

    @Override
    public Teacher selectById(Object primaryValue) {
        return TEACHERS.get(Long.valueOf(primaryValue.toString()));
    }

    @Override
    public List<Teacher> selectByIds(List<?> ids) {
        return ids.stream()
            .map(this::selectById)
            .filter(Objects::nonNull)
            .toList();
    }
}
