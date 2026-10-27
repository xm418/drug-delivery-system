package com.medical.drugdelivery.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 注册请求参数
 * 注册接口用，用户名、密码、真实姓名必填，手机号和所属机构选填
 */
@Data
public class RegisterDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    /** 手机号，选填 */
    private String phone;

    /** 所属医疗机构id，选填，标识这个用户是哪家医院的 */
    private Long institutionId;
}
