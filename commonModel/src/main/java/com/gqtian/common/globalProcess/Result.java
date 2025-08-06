package com.gqtian.common.globalProcess;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.gqtian.common.constant.Constant.DATE_FORMAT_TYPE;
import com.gqtian.common.enums.ResultCodeEnum;

import cn.hutool.core.date.DateUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 统一返回格式
 *
 * @author gqtian
 * @date 2023/04/26 10:02
 */
@Data
@Slf4j
public class Result<T> implements Serializable {
    
    /**
     * 获取
     */
    private static Properties jsonProp = new Properties();
    
    /*
     * 静态代码块只在类加载的时候执行一次
     */
    static {
        try {
            jsonProp = PropertiesLoaderUtils.loadProperties(new ClassPathResource("messages.properties"));
            log.info("messages.properties ...初始化成功!");
        }
        catch (IOException e) {
            log.error("messages.properties ...加载失败!");
        }
    }
    
    /**
     * 是否返回成功
     */
    private boolean success;
    
    /**
     * 错误状态
     */
    private String code;
    
    /***
     * 错误信息
     */
    private String msg;
    
    /**
     * 返回数据
     */
    private T data;
    
    /**
     * 时间戳
     */
    private String timestamp;
    
    public Result()
    {
        this.timestamp = DateUtil.format(new Date(), DATE_FORMAT_TYPE.DATE_TIME);
    }
    
    /**
     * 成功的操作
     */
    public static <T> Result<T> success() {
        return success(null);
    }
    
    /**
     * 成 功 操 作 , 携 带 数 据
     */
    public static <T> Result<T> success(T data) {
        return success(ResultCodeEnum.RC100.getMessage(), data);
    }
    
    /**
     * 成 功 操 作, 携 带 消 息
     */
    public static <T> Result<T> success(String message) {
        return success(message, null);
    }
    
    /**
     * 成 功 操 作, 携 带 消 息 和 携 带 数 据
     */
    public static <T> Result<T> success(String message, T data) {
        return success(ResultCodeEnum.RC100.getCode(), message, data);
    }
    
    /**
     * 成 功 操 作, 携 带 自 定 义 状 态 码 和 消 息
     */
    public static <T> Result<T> success(String code, String message) {
        return success(code, message, null);
    }
    
    public static <T> Result<T> success(String code, String message, T data) {
        Result<T> result = new Result<T>();
        result.setCode(code);
        result.setMsg(message);
        result.setSuccess(true);
        result.setData(data);
        return result;
    }
    
    /**
     * 失 败 操 作, 默 认 数 据
     */
    public static <T> Result<T> failure() {
        return failure(ResultCodeEnum.RC100.getMessage());
    }
    
    /**
     * 失 败 操 作, 携 带 自 定 义 消 息
     */
    public static <T> Result<T> failure(String message) {
        return failure(ResultCodeEnum.RC999.getCode(), message);
    }
    
    /**
     * 失 败 操 作, 携 带 自 定 义 消 息 和 数 据
     */
    public static <T> Result<T> failure(String message, T data) {
        return failure(ResultCodeEnum.RC999.getCode(), message, data);
    }
    
    /**
     * 失 败 操 作, 携 带 自 定 义 状 态 码 和 自 定 义 消 息
     */
    public static <T> Result<T> failure(String code, String message) {
        return failure(code, message, null);
    }
    
    /**
     * 失 败 操 作, 携 带 自 定 义 状 态 码 , 消 息 和 数 据
     */
    public static <T> Result<T> failure(String code, String message, T data) {
        Result<T> result = new Result<T>();
        result.setCode(code);
        result.setMsg(message);
        result.setSuccess(false);
        result.setData(data);
        return result;
    }
    
    /**
     * Boolean 返 回 操 作, 携 带 默 认 返 回 值
     */
    public static <T> Result<T> decide(boolean b) {
        return decide(b, ResultCodeEnum.RC100.getMessage(), ResultCodeEnum.RC999.getMessage());
    }
    
    /**
     * Boolean 返 回 操 作, 携 带 自 定 义 消 息
     */
    public static <T> Result<T> decide(boolean b, String success, String failure) {
        if (b) {
            return success(success);
        }
        else {
            return failure(failure);
        }
    }
}
