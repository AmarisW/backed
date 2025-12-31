package com.gqtian.mybatis.config;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.function.Supplier;

import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * 🤨 字段自动填充
 * @className MyMetaObjectHandler
 * @author gqtian
 * @date 2025/12/23 16:05
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入填充
     * @param metaObject metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        fillFieldByAnnotation(metaObject, field -> {
            FieldFill fill = field.fill();
            return fill == FieldFill.INSERT || fill == FieldFill.INSERT_UPDATE;
        }, true);
    }

    /**
     * 插入填充
     * @param metaObject metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        fillFieldByAnnotation(metaObject, field -> {
            FieldFill fill = field.fill();
            return fill == FieldFill.UPDATE || fill == FieldFill.INSERT_UPDATE;
        }, false);
    }

    /**
     * 通用字段填充方法
     * @param metaObject 元数据对象
     * @param fillPredicate 填充条件断言
     * @param checkNull 是否检查字段为空
     */
    private void fillFieldByAnnotation(MetaObject metaObject,
        java.util.function.Predicate<TableField> fillPredicate, boolean checkNull) {
        Arrays.stream(metaObject.getOriginalObject().getClass().getDeclaredFields())
            .filter(field -> field.isAnnotationPresent(TableField.class))
            .filter(field -> fillPredicate.test(field.getAnnotation(TableField.class)))
            .forEach(field -> fillField(metaObject, field, checkNull));
    }

    /**
     * 填充字段
     */
    private void fillField(MetaObject metaObject, Field field, boolean checkNull) {
        String fieldName = field.getName();
        if (!metaObject.hasSetter(fieldName)) {
            return;
        }
        // 如果需要检查空值且字段已有值，则不填充
        if (checkNull && metaObject.getValue(fieldName) != null) {
            return;
        }
        // 获取填充值
        Object value = getFieldValue(field.getType());
        if (value != null) {
            this.setFieldValByName(fieldName, value, metaObject);
        }
    }

    /**
     * 根据字段类型获取填充值
     */
    private Object getFieldValue(Class<?> fieldType) {
        Supplier<?> supplier = getValueSupplier(fieldType);
        return supplier != null ? supplier.get() : null;
    }

    /**
     * 获取值提供者
     */
    private Supplier<?> getValueSupplier(Class<?> fieldType) {
        if (fieldType == Date.class) {
            return Date::new;
        }
        if (fieldType == LocalDateTime.class) {
            return LocalDateTime::now;
        }
        if (fieldType == Long.class) {
            return System::currentTimeMillis;
        }
        // TODO 可以根据需要扩展其他类型
        return null;
    }
}
