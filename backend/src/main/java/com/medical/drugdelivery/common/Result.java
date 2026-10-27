package com.medical.drugdelivery.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回结果包装类
 * 所有接口返回给前端的数据都用这个包一层，格式固定为 code/message/data
 * 前端只要看code就知道成功还是失败
 */
@Data
public class Result<T> implements Serializable {

    /** 状态码，200成功，其他都是失败，具体看ResultCode */
    private Integer code;

    /** 提示信息，失败的时候前端直接弹出来给用户看 */
    private String message;

    /** 真正返回的数据，失败的时候一般是null */
    private T data;

    /** 成功，不带数据 */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        return result;
    }

    /** 成功，带数据 */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }

    /** 成功，自定义提示信息并带数据 */
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    /** 失败，用默认的"操作失败"提示 */
    public static <T> Result<T> error() {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.ERROR.getCode());
        result.setMessage(ResultCode.ERROR.getMessage());
        return result;
    }

    /** 失败，自定义提示信息，错误码默认500 */
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.ERROR.getCode());
        result.setMessage(message);
        return result;
    }

    /** 失败，自定义错误码和提示信息 */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
