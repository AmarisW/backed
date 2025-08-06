package com.gqtian.common.globalProcess;

import com.gqtian.common.enums.ResultCodeEnum;

/**
 * 自定义业务异常
 *
 * @author gqtian
 * @date 2023/04/26 10:52
 */
public class BusinessException extends RuntimeException {
    
    public BusinessException()
    {
        super();
    }
    
    public BusinessException(int messageCode)
    {
        super(String.valueOf(messageCode));
    }
    
    public BusinessException(String message)
    {
        super(message);
    }
    
    public BusinessException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public BusinessException(Throwable cause)
    {
        super(cause);
    }
    
    public BusinessException(ResultCodeEnum resultCodeEnum)
    {
        super(resultCodeEnum.getCode());
    }
}
