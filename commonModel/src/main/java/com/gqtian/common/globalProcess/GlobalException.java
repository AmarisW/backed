package com.gqtian.common.globalProcess;

import com.gqtian.common.enums.GlobalResultCodeEnum;

/**
 * 全局自定义业务异常
 * <p>
 * 用于封装业务逻辑处理中的异常情况，支持多种异常创建方式：
 * <ul>
 *   <li>无参构造</li>
 *   <li>通过错误码构造</li>
 *   <li>通过错误信息构造</li>
 *   <li>通过错误信息和原始异常构造</li>
 *   <li>通过原始异常构造</li>
 *   <li>通过统一返回状态码枚举构造</li>
 * </ul>
 * </p>
 *
 * @author gqtian
 * @date 2023/04/26 10:52
 */
public class GlobalException extends RuntimeException {

    /**
     * 无参构造方法
     */
    public GlobalException() {
        super();
    }

    /**
     * 通过错误码构造异常
     * 
     * @param messageCode 错误码
     */
    public GlobalException(int messageCode) {
        super(String.valueOf(messageCode));
    }

    /**
     * 通过错误信息构造异常
     * 
     * @param message 错误信息
     */
    public GlobalException(String message) {
        super(message);
    }

    /**
     * 通过错误信息和原始异常构造
     * 
     * @param message 错误信息
     * @param cause 原始异常
     */
    public GlobalException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 通过原始异常构造
     * 
     * @param cause 原始异常
     */
    public GlobalException(Throwable cause) {
        super(cause);
    }

    /**
     * 通过统一返回状态码枚举构造异常
     * 
     * @param globalResultCodeEnum 状态码枚举
     */
    public GlobalException(GlobalResultCodeEnum globalResultCodeEnum) {
        super(globalResultCodeEnum.getCode());
    }
}
