package com.medical.drugdelivery.config;

import com.medical.drugdelivery.common.ResultCode;
import lombok.Getter;

/**
 * 自定义业务异常
 * Service层遇到业务上的问题（库存不够、用户名重复之类的）就抛这个，
 * 最后由GlobalExceptionHandler统一接住返回给前端
 */
@Getter
public class BusinessException extends RuntimeException {

    /** 错误码，对应ResultCode里定义的那些 */
    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.ERROR.getCode();
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }
}
