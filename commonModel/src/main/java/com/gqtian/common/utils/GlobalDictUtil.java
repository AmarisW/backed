package com.gqtian.common.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gqtian.common.cache.GlobalCache;
import com.gqtian.common.model.GlobalDict;

import lombok.extern.slf4j.Slf4j;

/**
 * 数据字典工具类
 *
 * @author gqtian
 * @date 2023/04/26 13:43
 */
@Slf4j
public class GlobalDictUtil {

    /**
     * codeToMc:page对象编码转名称.
     *
     * @return Page<T>
     */
    public static <T> IPage<T> translateDic(IPage<T> page) {
        page.setRecords(translateDic(page.getRecords()));
        return page;
    }

    /**
     * codeToMc:list对象编码转名称. <br>
     *
     * @return List<T>
     */
    public static <T> List<T> translateDic(List<T> list) {
        return downOperate(list);
    }

    /**
     * 批量处理字段
     * @param list 列表
     * @return List<T>
     * @param <T> 泛型
     */
    private static <T> List<T> downOperate(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list.stream().map(GlobalDictUtil::translateDic).collect(Collectors.toList());
    }

    /**
     * 字段处理
     * @param t 对象
     * @return  T
     * @param <T> 泛型
     */
    public static <T> T translateDic(T t) {
        if (t == null) {
            return null;
        }
        try {
            Class<?> aClass = t.getClass();
            Field[] fields = aClass.getDeclaredFields();

            for (Field field : fields) {
                Class<?> type = field.getType();
                field.setAccessible(true);

                // 处理自定义对象类型
                if (type.getClassLoader() != null) {
                    Object object = field.get(t);
                    if (object != null) {
                        translateDic(object);
                    }
                    continue;
                }

                // 处理List类型字段
                if (List.class.isAssignableFrom(type)) {
                    @SuppressWarnings("unchecked")
                    List<Object> list = (List<Object>)field.get(t);
                    if (!CollectionUtils.isEmpty(list)) {
                        list.forEach(item -> {
                            if (item != null && item.getClass().getClassLoader() != null) {
                                translateDic(item);
                            }
                        });
                    }
                    continue;
                }

                // 处理基本类型和String类型字段
                handleField(t, field, aClass);
            }
        } catch (Exception e) {
            log.error("字典转换失败，对象类型：{}, 错误信息：{}", t.getClass().getName(), e.getMessage(), e);
        }
        return t;
    }

    /**
     * 字段处理
     * @param t 对象
     * @param field 字段
     * @param aClass  类
     * @param <T> 泛型
     */
    private static <T> void handleField(T t, Field field, Class<?> aClass)
        throws IllegalAccessException, NoSuchFieldException {
        if (field.isAnnotationPresent(com.gqtian.common.annotation.GlobalDict.class)) {
            com.gqtian.common.annotation.GlobalDict translateDic =
                field.getAnnotation(com.gqtian.common.annotation.GlobalDict.class);
            field.setAccessible(true);
            Object object = field.get(t);

            // 获取字段值
            String code = object == null ? null : object.toString();
            String dicType = StringUtils.EMPTY;

            assert translateDic != null;
            String annotationDicType = translateDic.type();
            if (StringUtils.isNotBlank(annotationDicType)) {
                dicType = annotationDicType;
            } else {
                Object objectDicType = field.get(t);
                if (objectDicType != null) {
                    dicType = objectDicType.toString();
                }
            }

            String dicString = getDicString(dicType, code);
            Field codeField = aClass.getDeclaredField(field.getName() + "Txt");
            // 如果codeField存在
            codeField.setAccessible(true);

            if (StringUtils.isNotBlank(dicString)) {
                if (codeField.getType().isAssignableFrom(String.class)) {
                    codeField.set(t, dicString);
                } else {
                    log.warn("字段 {} 类型不支持设置字典值：{}", field.getName(), field.getType());
                }
            } else {
                codeField.set(t, StringUtils.EMPTY);
            }
        }
    }

    /**
     * 获取指定字典
     *
     * @param type 字典类型
     * @param code 编码
     * @return 字典对象
     */
    private static String getDicString(String type, String code) {
        List<GlobalDict> sysDicList = listDictionary(type);

        if (sysDicList.isEmpty()) {
            return StringUtils.EMPTY;
        }

        // 构建字典Map提高查询效率
        Map<String, String> dictMap = sysDicList.stream()
            .collect(Collectors.toMap(GlobalDict::getCode, GlobalDict::getValue, (existing, replacement) -> existing));

        // 安全分割字符串
        List<String> split = new ArrayList<>();
        if (StringUtils.isNotBlank(code)) {
            split = Arrays.asList(code.split(","));
        }

        // 查找并拼接结果
        List<String> valueName = new ArrayList<>();
        for (String s : split) {
            String value = dictMap.get(s);
            if (value != null) {
                valueName.add(value);
            }
        }

        return String.join(",", valueName);
    }

    /**
     * 根据类型获取所有字典（返回不可变副本）
     *
     * @param type 字典类型
     * @return List<SysDic>
     */
    public static List<GlobalDict> listDictionary(String type) {
        List<GlobalDict> list = GlobalCache.GLOBAL_DICT.get(type);
        return list == null ? Collections.emptyList() : new ArrayList<>(list);
    }

}
