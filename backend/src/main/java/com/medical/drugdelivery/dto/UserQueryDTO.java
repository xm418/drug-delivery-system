package com.medical.drugdelivery.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户分页查询参数
 * 可以按账号状态、角色、所属机构筛选，关键字按用户名/姓名模糊查
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserQueryDTO extends PageQueryDTO {

    /** 账号状态：1正常 0禁用 */
    private Integer status;

    /** 角色id */
    private Integer roleId;

    /** 所属机构id */
    private Long institutionId;
}
