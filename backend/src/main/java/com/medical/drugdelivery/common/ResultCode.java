package com.medical.drugdelivery.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 返回状态码枚举
 * 统一管理系统里所有的code和提示信息
 * 1开头是用户相关的，2开头是业务相关的（库存、订单、药品、机构）
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),

    LOGIN_ERROR(1001, "用户名或密码错误"),
    USER_DISABLED(1002, "用户已被禁用"),
    TOKEN_INVALID(1003, "Token无效或已过期"),
    USER_NOT_FOUND(1004, "用户不存在"),
    USER_EXISTS(1005, "用户名已存在"),

    INSUFFICIENT_STOCK(2001, "库存不足"),
    ORDER_STATUS_ERROR(2002, "订单状态错误"),
    DRUG_NOT_FOUND(2003, "药品不存在"),
    INSTITUTION_NOT_FOUND(2004, "医疗机构不存在");

    private final Integer code;
    private final String message;
}
