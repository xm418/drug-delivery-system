package com.medical.drugdelivery.service;

import com.medical.drugdelivery.common.PageResult;
import com.medical.drugdelivery.dto.PageQueryDTO;
import com.medical.drugdelivery.entity.SysRole;

import java.util.List;

/**
 * 角色Service接口
 * 系统里的角色管理，用户靠角色区分权限（管理员、机构管理员、配送员等）
 */
public interface RoleService {

    /** 查所有角色，给用户分配角色时的下拉框用 */
    List<SysRole> listAll();

    /** 分页查询角色列表 */
    PageResult<SysRole> page(PageQueryDTO queryDTO);

    /** 根据id查角色 */
    SysRole getById(Long id);

    /** 新增角色 */
    void add(SysRole role);

    /** 修改角色 */
    void update(SysRole role);

    /** 删除角色（已经有用户用的角色不让删） */
    void delete(Long id);
}
