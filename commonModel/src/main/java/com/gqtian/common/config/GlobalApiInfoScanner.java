package com.gqtian.common.config;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter.Feature;
import com.gqtian.common.annotation.GlobalApiOut;
import com.gqtian.common.model.GlobalApiOutEntity;
import com.gqtian.common.model.GlobalApiOutEntity.ParamEntity;

import lombok.extern.slf4j.Slf4j;

/**
 * API信息扫描器
 * <p>
 * 在项目启动时扫描所有带有@GlobalApi注解的方法，并打印其请求路径、注释信息、入参和返回值信息
 * </p>
 *
 * @author gqtian
 */
@Slf4j
@Configuration
public class GlobalApiInfoScanner implements ImportAware {
    
    // HTTP方法常量
    private static final String HTTP_METHOD_GET = "GET";
    
    private static final String HTTP_METHOD_POST = "POST";
    
    private static final String HTTP_METHOD_PUT = "PUT";
    
    private static final String HTTP_METHOD_DELETE = "DELETE";
    
    private static final String HTTP_METHOD_PATCH = "PATCH";
    
    private static final String HTTP_METHOD_REQUEST = "REQUEST";
    
    // 参数信息常量
    private static final String NO_PARAMETERS = "无参数";
    
    // 映射注解类型到HTTP方法
    private static final Class<? extends Annotation>[] MAPPING_ANNOTATIONS = new Class[]
    {GetMapping.class, PostMapping.class, PutMapping.class, DeleteMapping.class, PatchMapping.class,
        RequestMapping.class};
    
    // HTTP方法映射
    private static final String[] HTTP_METHODS = new String[]
    {HTTP_METHOD_GET, HTTP_METHOD_POST, HTTP_METHOD_PUT, HTTP_METHOD_DELETE, HTTP_METHOD_PATCH, HTTP_METHOD_REQUEST};
    
    @Override
    public void setImportMetadata(@NonNull
    AnnotationMetadata importingClassMetadata) {
        // 当注解被导入时，执行API信息扫描
        scanAndPrintApiInfo();
    }
    
    /**
     * 扫描并打印API信息
     */
    private void scanAndPrintApiInfo() {
        try {
            // 创建扫描器
            ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
            
            // 添加过滤器，只扫描RestController注解的类
            scanner.addIncludeFilter(new AnnotationTypeFilter(RestController.class));
            
            // 扫描所有RestController类
            Set<BeanDefinition> beans = scanner.findCandidateComponents("com");
            
            // 遍历所有RestController类
            for (BeanDefinition bean : beans) {
                try {
                    Class<?> clazz = Class.forName(bean.getBeanClassName());
                    printClassApiInfo(clazz);
                }
                catch (Exception e) {
                    log.warn("处理类 {} 时发生异常: {}", bean.getBeanClassName(), e.getMessage());
                }
            }
        }
        catch (Exception e) {
            log.error("扫描API接口信息时发生异常", e);
        }
    }
    
    /**
     * 打印类中带有@GlobalApi注解的方法信息
     *
     * @param clazz 类
     */
    private void printClassApiInfo(Class<?> clazz) {
        String classRequestMapping = getClassRequestMapping(clazz);
        
        // 遍历所有方法
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(GlobalApiOut.class)) {
                printMethodApiInfo(method, classRequestMapping);
            }
        }
    }
    
    /**
     * 获取类上的RequestMapping路径
     * 
     * @param clazz 类
     * @return RequestMapping路径
     */
    private String getClassRequestMapping(Class<?> clazz) {
        RequestMapping requestMapping = AnnotationUtils.findAnnotation(clazz, RequestMapping.class);
        if (requestMapping != null && requestMapping.value().length > 0) {
            return requestMapping.value()[0];
        }
        return Strings.EMPTY;
    }
    
    /**
     * 打印方法的API信息
     * 
     * @param method 方法
     * @param classRequestMapping 类上的RequestMapping路径
     */
    private void printMethodApiInfo(Method method, String classRequestMapping) {
        String httpMethod = "";
        String methodRequestMapping = "";
        
        // 统一处理各种Mapping注解
        for (int i = 0; i < MAPPING_ANNOTATIONS.length; i++) {
            Class<? extends Annotation> annotationClass = MAPPING_ANNOTATIONS[i];
            if (method.isAnnotationPresent(annotationClass)) {
                httpMethod = HTTP_METHODS[i];
                methodRequestMapping = getMappingValue(method, annotationClass);
                break;
            }
        }
        
        // 完整路径
        String fullPath = classRequestMapping + methodRequestMapping;
        GlobalApiOut apiInfo = method.getAnnotation(GlobalApiOut.class);
        
        // 描述和参数信息
        String comment = apiInfo != null ? apiInfo.value() : null;
        List<ParamEntity> params = getMethodParameters(method);
        
        // 添加到队列缓存
        GlobalBeautifyStart.GLOBAL_API_CACHE.add(
            GlobalApiOutEntity.builder().httpMethod(httpMethod).path(fullPath).params(params).comment(comment).build());
    }
    
    /**
     * 获取Mapping注解的路径值
     * 
     * @param method 方法
     * @param annotationClass 注解类
     * @return 路径值
     */
    private String getMappingValue(Method method, Class<? extends Annotation> annotationClass) {
        Annotation annotation = method.getAnnotation(annotationClass);
        if (annotation != null) {
            String[] value = (String[])AnnotationUtils.getValue(annotation, "value");
            if (value != null && value.length > 0) {
                return value[0];
            }
        }
        return "";
    }
    
    /**
     * 获取方法的参数信息
     * 
     * @param method 方法
     * @return 参数信息字符串
     */
    private List<ParamEntity> getMethodParameters(Method method) {
        ArrayList<ParamEntity> params = new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        if (parameters.length == 0) {
            return new ArrayList<>();
        }
        
        for (Parameter parameter : parameters) {
            // 获取参数类型
            Class<?> paramType = parameter.getType();
            if (isSimpleType(paramType)) {
                ParamEntity param =
                    ParamEntity.builder().name(parameter.getName()).type(paramType.getSimpleName()).build();
                params.add(param);
            }
            else {
                ParamEntity param =
                    ParamEntity.builder().name(generateJsonExample(paramType)).type(paramType.getSimpleName()).build();
                params.add(param);
            }
        }
        
        return params;
    }
    
    /**
     * 生成实体类的JSON示例
     * 
     * @param type 实体类类型
     * @return JSON示例字符串
     */
    private String generateJsonExample(Class<?> type) {
        try {
            // 创建实例
            Object instance = type.getDeclaredConstructor().newInstance();
            // 转换为格式化的JSON字符串
            return JSON.toJSONString(instance, Feature.WriteMapNullValue);
        }
        catch (Exception e) {
            log.warn("无法为类型 {} 生成JSON示例: {}", type.getSimpleName(), e.getMessage());
            return null;
        }
    }
    
    /**
     * 判断是否为简单类型
     * 
     * @param type 类型
     * @return 是否为简单类型
     */
    private boolean isSimpleType(Class<?> type) {
        return type.isPrimitive() || type == String.class || type == Integer.class || type == Long.class
            || type == Double.class || type == Float.class || type == Boolean.class || type == Character.class
            || type == Byte.class || type == Short.class || type.isEnum();
    }
}