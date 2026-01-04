package com.gqtian.common.enums;

import lombok.Getter;

/**
 * 全局统一返回状态码枚举
 * <p>
 * 定义系统中所有API接口的统一返回状态码和描述信息
 * 包括成功、失败、限流、降级、权限、认证等各种场景
 * </p>
 *
 * @author gqtian
 * @date 2023/04/26 10:04
 */
@Getter
public enum GlobalResultCodeEnum {
    /**
     * 状态码：空
     */
    RC000("000", ""),
    
    /**
     * 状态码：操作成功
     */
    RC100("100", "操作成功"),
    
    /**
     * 状态码：操作失败
     */
    RC999("999", "操作失败"),
    
    /**
     * 状态码：服务限流
     */
    RC200("200", "服务开启限流保护,请稍后再试!"),
    
    /**
     * 状态码：服务降级
     */
    RC201("201", "服务开启降级保护,请稍后再试!"),
    
    /**
     * 状态码：热点参数限流
     */
    RC202("202", "热点参数限流,请稍后再试!"),
    
    /**
     * 状态码：系统规则不满足
     */
    RC203("203", "系统规则不满足要求,请稍后再试!"),
    
    /**
     * 状态码：授权规则不通过
     */
    RC204("204", "授权规则不通过,请稍后再试!"),
    
    /**
     * 状态码：无访问权限
     */
    RC403("403", "无访问权限,请联系管理员授予权限"),
    
    /**
     * 状态码：匿名用户访问无权限资源
     */
    RC401("401", "匿名用户访问无权限资源时的异常"),
    
    /**
     * 状态码：系统异常
     */
    RC500("500", "系统异常，请稍后重试"),
    
    /**
     * 状态码：访问令牌不合法
     */
    INVALID_TOKEN("2001", "访问令牌不合法"),
    
    /**
     * 状态码：没有权限访问该资源
     */
    ACCESS_DENIED("2003", "没有权限访问该资源"),
    
    /**
     * 状态码：客户端认证失败
     */
    CLIENT_AUTHENTICATION_FAILED("1001", "客户端认证失败"),
    
    /**
     * 状态码：用户名或密码错误
     */
    USERNAME_OR_PASSWORD_ERROR("1002", "用户名或密码错误"),
    
    /**
     * 状态码：不支持的认证模式
     */
    UNSUPPORTED_GRANT_TYPE("1003", "不支持的认证模式");

    /**
     * 状态码
     */
    private final String code;

    /**
     * 描述信息
     */
    private final String message;

    /**
     * 构造方法
     * 
     * @param code 状态码
     * @param message 描述信息
     */
    GlobalResultCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据状态码获取对应的枚举对象
     * 
     * @param code 状态码
     * @return 对应的枚举对象，找不到返回null
     */
    public static GlobalResultCodeEnum getResultCodeEnum(String code) {
        for (GlobalResultCodeEnum globalResultCodeEnum : GlobalResultCodeEnum.values()) {
            if (globalResultCodeEnum.getCode().equals(code)) {
                return globalResultCodeEnum;
            }
        }
        return null;
    }
}
