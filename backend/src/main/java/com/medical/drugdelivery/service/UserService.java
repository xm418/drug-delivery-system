package com.medical.drugdelivery.service;

import com.medical.drugdelivery.common.PageResult;
import com.medical.drugdelivery.dto.LoginDTO;
import com.medical.drugdelivery.dto.PasswordDTO;
import com.medical.drugdelivery.dto.RegisterDTO;
import com.medical.drugdelivery.dto.UserQueryDTO;
import com.medical.drugdelivery.entity.SysUser;

import java.util.Map;

/**
 * 用户Service接口
 * 登录注册、用户管理、改密码这些
 */
public interface UserService {

    /** 登录，校验用户名密码，成功后返回token和用户信息 */
    Map<String, Object> login(LoginDTO loginDTO);

    /** 注册，用户名不能重复，密码MD5加密后存库 */
    void register(RegisterDTO registerDTO);

    /** 根据id查用户 */
    SysUser getById(Long id);

    /** 分页查询用户列表 */
    PageResult<SysUser> page(UserQueryDTO queryDTO);

    /** 新增用户（管理员后台添加） */
    void add(SysUser user);

    /** 修改用户信息 */
    void update(SysUser user);

    /** 删除用户（逻辑删除） */
    void delete(Long id);

    /** 修改自己的密码，要先校验旧密码 */
    void updatePassword(Long userId, PasswordDTO passwordDTO);

    /** 重置密码，管理员把用户密码重置成默认的123456 */
    void resetPassword(Long id);
}
