package com.gqtian.common.globalProcess;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gqtian.common.constant.GlobalConstant.DATE_FORMAT_TYPE;
import com.gqtian.common.enums.GlobalResultCodeEnum;

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
@JsonInclude(JsonInclude.Include.NON_NULL) // 忽略为空的字段
public class GlobalResult<T> implements Serializable {

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

    public GlobalResult() {
        this.timestamp = DateUtil.format(new Date(), DATE_FORMAT_TYPE.DATE_TIME);
    }

    /**
     * 成功的操作
     */
    public static <T> GlobalResult<T> success() {
        return success(null);
    }

    /**
     * 成 功 操 作 , 携 带 数 据
     */
    public static <T> GlobalResult<T> success(T data) {
        return success(GlobalResultCodeEnum.RC100.getMessage(), data);
    }

    /**
     * 成 功 操 作, 携 带 消 息
     */
    public static <T> GlobalResult<T> success(String message) {
        return success(message, null);
    }

    /**
     * 成 功 操 作, 携 带 消 息 和 携 带 数 据
     */
    public static <T> GlobalResult<T> success(String message, T data) {
        return success(GlobalResultCodeEnum.RC100.getCode(), message, data);
    }

    /**
     * 成 功 操 作, 携 带 自 定 义 状 态 码 和 消 息
     */
    public static <T> GlobalResult<T> success(String code, String message) {
        return success(code, message, null);
    }

    public static <T> GlobalResult<T> success(String code, String message, T data) {
        GlobalResult<T> globalResult = new GlobalResult<>();
        globalResult.setCode(code);
        globalResult.setMsg(message);
        globalResult.setSuccess(true);
        globalResult.setData(data);
        return globalResult;
    }

    /**
     * 失 败 操 作, 默 认 数 据
     */
    public static <T> GlobalResult<T> failure() {
        return failure(GlobalResultCodeEnum.RC100.getMessage());
    }

    /**
     * 失 败 操 作, 携 带 自 定 义 消 息
     */
    public static <T> GlobalResult<T> failure(String message) {
        return failure(GlobalResultCodeEnum.RC999.getCode(), message);
    }

    /**
     * 失 败 操 作, 携 带 自 定 义 消 息 和 数 据
     */
    public static <T> GlobalResult<T> failure(String message, T data) {
        return failure(GlobalResultCodeEnum.RC999.getCode(), message, data);
    }

    /**
     * 失 败 操 作, 携 带 自 定 义 状 态 码 和 自 定 义 消 息
     */
    public static <T> GlobalResult<T> failure(String code, String message) {
        return failure(code, message, null);
    }

    /**
     * 失 败 操 作, 携 带 自 定 义 状 态 码 , 消 息 和 数 据
     */
    public static <T> GlobalResult<T> failure(String code, String message, T data) {
        GlobalResult<T> globalResult = new GlobalResult<T>();
        globalResult.setCode(code);
        globalResult.setMsg(message);
        globalResult.setSuccess(false);
        globalResult.setData(data);
        return globalResult;
    }

    /**
     * Boolean 返 回 操 作, 携 带 默 认 返 回 值
     */
    public static <T> GlobalResult<T> decide(boolean b) {
        return decide(b, GlobalResultCodeEnum.RC100.getMessage(), GlobalResultCodeEnum.RC999.getMessage());
    }

    /**
     * Boolean 返 回 操 作, 携 带 自 定 义 消 息
     */
    public static <T> GlobalResult<T> decide(boolean b, String success, String failure) {
        if (b) {
            return success(success);
        } else {
            return failure(failure);
        }
    }
}