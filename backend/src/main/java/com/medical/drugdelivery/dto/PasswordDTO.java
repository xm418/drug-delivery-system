package com.medical.drugdelivery.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 修改密码请求参数
 * 个人中心里改密码用，要传旧密码校验身份
 */
@Data
public class PasswordDTO {

    @NotNull(message = "旧密码不能为空")
    private String oldPassword;

    @NotNull(message = "新密码不能为空")
    private String newPassword;
}
