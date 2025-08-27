package com.gqtian.common.globalProcess;

import com.gqtian.common.enums.GlobalResultCodeEnum;

/**
 * 自定义业务异常
 *
 * @author gqtian
 * @date 2023/04/26 10:52
 */
public class GlobalException extends RuntimeException {

    public GlobalException() {
        super();
    }

    public GlobalException(int messageCode) {
        super(String.valueOf(messageCode));
    }

    public GlobalException(String message) {
        super(message);
    }

    public GlobalException(String message, Throwable cause) {
        super(message, cause);
    }

    public GlobalException(Throwable cause) {
        super(cause);
    }

    public GlobalException(GlobalResultCodeEnum globalResultCodeEnum) {
        super(globalResultCodeEnum.getCode());
    }
}
