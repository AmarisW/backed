package com.gqtian.common.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gqtian.common.cache.GlobalCache;
import com.gqtian.common.model.GlobalDict;

import lombok.extern.slf4j.Slf4j;

/**
 * 数据字典工具类
 * <p>
 * 用于将对象中带有 @GlobalDict 注解的字段自动翻译为对应的文本值
 * </p>
 *
 * @author gqtian
 * @date 2023/04/26 13:43
 */
@Slf4j
public class GlobalDictUtil {

    /**
     * 字段元数据缓存
     * Key: 类的全限定名
     * Value: 该类所有字段的元数据列表（包含父类字段）
     */
    private static final Map<String, List<FieldMeta>> FIELD_CACHE = new ConcurrentHashMap<>();

    /**
     * 字典Map缓存（用于批量处理时避免重复构建）
     * Key: 字典类型
     * Value: code -> value 的映射
     */
    private static final Map<String, Map<String, String>> DICT_MAP_CACHE = new ConcurrentHashMap<>();

    /**
     * 字段元数据内部类
     *
     * @param sourceField    源字段（带注解的字段）
     * @param targetField    目标字段（xxxTxt字段）
     * @param dictType       字典类型
     * @param emptyText      空值默认文本
     * @param isNestedObject 是否为嵌套对象
     * @param isList         是否为List类型
     */
    private record FieldMeta(Field sourceField, Field targetField, String dictType, String emptyText,
        boolean isNestedObject, boolean isList) {

    }

    /**
     * Page对象编码转名称
     *
     * @param page 分页对象
     * @param <T>  泛型
     * @return 翻译后的分页对象
     */
    public static <T> IPage<T> translateDic(IPage<T> page) {
        if (page != null) {
            page.setRecords(translateDic(page.getRecords()));
        }
        return page;
    }

    /**
     * List对象编码转名称
     *
     * @param list 列表
     * @param <T>  泛型
     * @return 翻译后的列表
     */
    public static <T> List<T> translateDic(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        // 清除字典Map缓存，确保每次批量处理使用最新数据
        DICT_MAP_CACHE.clear();
        try {
            return list.stream().map(GlobalDictUtil::translateDic).collect(Collectors.toList());
        } finally {
            // 批量处理完成后清除缓存
            DICT_MAP_CACHE.clear();
        }
    }

    /**
     * 单个对象字段翻译
     *
     * @param t   对象
     * @param <T> 泛型
     * @return 翻译后的对象
     */
    public static <T> T translateDic(T t) {
        if (t == null) {
            return null;
        }
        try {
            Class<?> clazz = t.getClass();
            List<FieldMeta> fieldMetas = getFieldMetas(clazz);

            for (FieldMeta meta : fieldMetas) {
                processField(t, meta);
            }
        } catch (Exception e) {
            log.error("字典转换失败，对象类型：{}, 错误信息：{}", t.getClass().getName(), e.getMessage(), e);
        }
        return t;
    }

    /**
     * 获取类的字段元数据（带缓存）
     *
     * @param clazz 类
     * @return 字段元数据列表
     */
    private static List<FieldMeta> getFieldMetas(Class<?> clazz) {
        String className = clazz.getName();
        return FIELD_CACHE.computeIfAbsent(className, k -> buildFieldMetas(clazz));
    }

    /**
     * 构建类的字段元数据
     *
     * @param clazz 类
     * @return 字段元数据列表
     */
    private static List<FieldMeta> buildFieldMetas(Class<?> clazz) {
        List<FieldMeta> metas = new ArrayList<>();
        List<Field> allFields = getAllFields(clazz);

        for (Field field : allFields) {
            field.setAccessible(true);
            Class<?> fieldType = field.getType();

            // 处理嵌套自定义对象
            if (isCustomClass(fieldType)) {
                metas.add(new FieldMeta(field, null, null, null, true, false));
                continue;
            }

            // 处理List类型
            if (List.class.isAssignableFrom(fieldType)) {
                metas.add(new FieldMeta(field, null, null, null, false, true));
                continue;
            }

            // 处理带有@GlobalDict注解的字段
            if (field.isAnnotationPresent(com.gqtian.common.annotation.GlobalDict.class)) {
                com.gqtian.common.annotation.GlobalDict annotation =
                    field.getAnnotation(com.gqtian.common.annotation.GlobalDict.class);

                String dictType = annotation.type();
                String emptyText = annotation.emptyText();

                // 查找对应的Txt字段
                Field targetField = findField(clazz, field.getName() + "Txt");
                if (targetField != null) {
                    targetField.setAccessible(true);
                    metas.add(new FieldMeta(field, targetField, dictType, emptyText, false, false));
                } else {
                    log.warn("字段 {} 缺少对应的 {}Txt 字段，跳过字典翻译", field.getName(), field.getName());
                }
            }
        }
        return metas;
    }

    /**
     * 获取类的所有字段（包含父类）
     *
     * @param clazz 类
     * @return 所有字段列表
     */
    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
            currentClass = currentClass.getSuperclass();
        }
        return fields;
    }

    /**
     * 查找字段（包含父类）
     *
     * @param clazz     类
     * @param fieldName 字段名
     * @return 字段，找不到返回null
     */
    private static Field findField(Class<?> clazz, String fieldName) {
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            try {
                return currentClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        return null;
    }

    /**
     * 判断是否为自定义类（非JDK类）
     *
     * @param clazz 类
     * @return 是否为自定义类
     */
    private static boolean isCustomClass(Class<?> clazz) {
        if (clazz == null || clazz.isPrimitive()) {
            return false;
        }
        String packageName = clazz.getName();
        return !packageName.startsWith("java.") && !packageName.startsWith("javax.")
            && !packageName.startsWith("sun.") && !clazz.isEnum() && !clazz.isArray();
    }

    /**
     * 处理单个字段
     *
     * @param t    对象
     * @param meta 字段元数据
     * @param <T>  泛型
     */
    private static <T> void processField(T t, FieldMeta meta) throws IllegalAccessException {
        // 处理嵌套对象
        if (meta.isNestedObject) {
            Object nestedObj = meta.sourceField.get(t);
            if (nestedObj != null) {
                translateDic(nestedObj);
            }
            return;
        }

        // 处理List类型
        if (meta.isList) {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>)meta.sourceField.get(t);
            if (!CollectionUtils.isEmpty(list)) {
                list.forEach(item -> {
                    if (item != null && isCustomClass(item.getClass())) {
                        translateDic(item);
                    }
                });
            }
            return;
        }

        // 处理字典翻译字段
        if (meta.targetField != null) {
            Object value = meta.sourceField.get(t);
            String code = value == null ? null : value.toString();
            String dictType = meta.dictType;

            // 如果注解未指定type，使用字段值作为type（保持原有逻辑兼容）
            if (StringUtils.isBlank(dictType)) {
                dictType = code;
            }

            String dictValue = getDicString(dictType, code);

            // 设置翻译值
            if (StringUtils.isNotBlank(dictValue)) {
                if (meta.targetField.getType().isAssignableFrom(String.class)) {
                    meta.targetField.set(t, dictValue);
                } else {
                    log.warn("字段 {}Txt 类型不是String，无法设置字典值", meta.sourceField.getName());
                }
            } else {
                // 使用注解配置的空值文本或空字符串
                String emptyValue =
                    StringUtils.isNotBlank(meta.emptyText) ? meta.emptyText : StringUtils.EMPTY;
                meta.targetField.set(t, emptyValue);
            }
        }
    }

    /**
     * 获取字典翻译值
     *
     * @param type 字典类型
     * @param code 编码（支持逗号分隔的多值）
     * @return 翻译后的值
     */
    private static String getDicString(String type, String code) {
        if (StringUtils.isBlank(type) || StringUtils.isBlank(code)) {
            return StringUtils.EMPTY;
        }

        // 获取或构建字典Map（带缓存）
        Map<String, String> dictMap = DICT_MAP_CACHE.computeIfAbsent(type, k -> {
            List<GlobalDict> dictList = listDictionary(k);
            if (dictList.isEmpty()) {
                return Collections.emptyMap();
            }
            return dictList.stream().collect(Collectors.toMap(GlobalDict::getCode, GlobalDict::getValue,
                (existing, replacement) -> existing));
        });

        if (dictMap.isEmpty()) {
            return StringUtils.EMPTY;
        }

        // 处理多值情况（逗号分隔）
        String[] codes = code.split(",");
        List<String> values = new ArrayList<>(codes.length);
        for (String c : codes) {
            String trimmedCode = c.trim();
            String value = dictMap.get(trimmedCode);
            if (value != null) {
                values.add(value);
            }
        }

        return String.join(",", values);
    }

    /**
     * 根据类型获取所有字典
     *
     * @param type 字典类型
     * @return 字典列表
     */
    public static List<GlobalDict> listDictionary(String type) {
        List<GlobalDict> list = GlobalCache.GLOBAL_DICT.get(type);
        return list == null ? Collections.emptyList() : new ArrayList<>(list);
    }
}
