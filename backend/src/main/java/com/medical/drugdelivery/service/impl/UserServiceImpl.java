package com.medical.drugdelivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.drugdelivery.common.Constants;
import com.medical.drugdelivery.common.PageResult;
import com.medical.drugdelivery.common.ResultCode;
import com.medical.drugdelivery.config.BusinessException;
import com.medical.drugdelivery.dto.LoginDTO;
import com.medical.drugdelivery.dto.PasswordDTO;
import com.medical.drugdelivery.dto.RegisterDTO;
import com.medical.drugdelivery.dto.UserQueryDTO;
import com.medical.drugdelivery.entity.Institution;
import com.medical.drugdelivery.entity.SysRole;
import com.medical.drugdelivery.entity.SysUser;
import com.medical.drugdelivery.mapper.InstitutionMapper;
import com.medical.drugdelivery.mapper.SysRoleMapper;
import com.medical.drugdelivery.mapper.SysUserMapper;
import com.medical.drugdelivery.service.UserService;
import com.medical.drugdelivery.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

// ============================================================
// 【CRUD Service 实现】UserServiceImpl
// 作用：业务逻辑层，调用 Mapper（继承 BaseMapper）执行数据库操作
//   add()    → 新增：补默认值 → mapper.insert()
//   page()   → 分页查询：构建 LambdaQueryWrapper 条件 → mapper.selectPage()
//   getById()→ 按 id 查详情：mapper.selectById() → 补关联表名称
//   update() → 修改：mapper.updateById()（主键 id 必填）
//   delete() → 删除：mapper.deleteById()（因 @TableLogic 实际是 UPDATE SET deleted=1）
// 事务：@Transactional 保证双表写入（如订单主表+明细）原子性
// ============================================================


/**
 * 用户Service实现类
 * 处理登录、注册、增删改查、密码相关
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private InstitutionMapper institutionMapper;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户登录
     * 1. 根据用户名查用户
     * 2. 比对密码（MD5加密的）
     * 3. 检查账号状态
     * 4. 生成token返回
     */
    @Override
    public Map<String, Object> login(LoginDTO loginDTO) {
        // 先根据用户名查用户
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, loginDTO.getUsername());
        SysUser user = userMapper.selectOne(wrapper);

        // 用户不存在
        if (user == null) {
            throw new BusinessException(ResultCode.LOGIN_ERROR);
        }

        // 比对密码，密码是MD5加密存的
        String password = DigestUtils.md5DigestAsHex(loginDTO.getPassword().getBytes(StandardCharsets.UTF_8));
        if (!password.equals(user.getPassword())) {
            throw new BusinessException(ResultCode.LOGIN_ERROR);
        }

        // 检查账号是否被禁用
        if (Constants.USER_STATUS_DISABLED.equals(user.getStatus())) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 生成JWT token，里面放用户id、用户名、角色id
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRoleId());

        // 组装返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("realName", user.getRealName());
        result.put("roleId", user.getRoleId());
        result.put("institutionId", user.getInstitutionId());

        return result;
    }

    /**
     * 用户注册
     * 先检查用户名是否存在，不存在就创建
     * 注册的用户默认角色是村医（roleId=3）
     */
    @Override
    // 【CRUD/业务】register()
    public void register(RegisterDTO registerDTO) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, registerDTO.getUsername());
        Long count = userMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.USER_EXISTS);
        }

        // 创建用户
        SysUser user = new SysUser();
        user.setUsername(registerDTO.getUsername());
        // 密码MD5加密后存
        user.setPassword(DigestUtils.md5DigestAsHex(registerDTO.getPassword().getBytes(StandardCharsets.UTF_8)));
        user.setRealName(registerDTO.getRealName());
        user.setPhone(registerDTO.getPhone());
        user.setInstitutionId(registerDTO.getInstitutionId());
        user.setStatus(Constants.USER_STATUS_NORMAL);
        user.setRoleId(3); // 默认村医角色
        userMapper.insert(user);
    }

    /**
     * 根据id查询用户
     * 顺便把角色名称和机构名称查出来
     */
    @Override
    public SysUser getById(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user != null) {
            fillUserInfo(user);
        }
        return user;
    }

    /**
     * 分页查询用户列表
     * 支持关键字搜索（用户名/真实姓名）、按状态/角色/机构筛选
     */
    @Override
    public PageResult<SysUser> page(UserQueryDTO queryDTO) {
        Page<SysUser> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

        // 关键字模糊搜索
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().equals("")) {
            wrapper.and(w -> {
                w.like(SysUser::getUsername, queryDTO.getKeyword());
                w.or().like(SysUser::getRealName, queryDTO.getKeyword());
            });
        }
        // 按状态筛选
        if (queryDTO.getStatus() != null) {
            wrapper.eq(SysUser::getStatus, queryDTO.getStatus());
        }
        // 按角色筛选
        if (queryDTO.getRoleId() != null) {
            wrapper.eq(SysUser::getRoleId, queryDTO.getRoleId());
        }
        // 按机构筛选
        if (queryDTO.getInstitutionId() != null) {
            wrapper.eq(SysUser::getInstitutionId, queryDTO.getInstitutionId());
        }
        // 按创建时间倒序
        wrapper.orderByDesc(SysUser::getCreateTime);

        Page<SysUser> result = userMapper.selectPage(page, wrapper);

        // 给每条记录填充角色名称和机构名称
        // 这里是循环一条条查的，数据多了可能慢
        // TODO: 后面可以改成联表查询
        for (SysUser user : result.getRecords()) {
            fillUserInfo(user);
        }

        return PageResult.of(result.getTotal(), result.getRecords(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    /**
     * 新增用户
     */
    @Override
    // 【CRUD】新增：补默认值 → mapper.insert()（MyBatis Plus 自动生成 INSERT）
    public void add(SysUser user) {
        // 检查用户名重复
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, user.getUsername());
        Long count = userMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.USER_EXISTS);
        }

        // 密码加密，没传就用默认密码123456
        String password = user.getPassword() != null ? user.getPassword() : Constants.DEFAULT_PASSWORD;
        user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8)));
        // 默认正常状态
        if (user.getStatus() == null) {
            user.setStatus(Constants.USER_STATUS_NORMAL);
        }
        userMapper.insert(user);
    }

    /**
     * 修改用户信息
     * 注意：修改的时候不能改密码，改密码有单独的接口
     */
    @Override
    // 【CRUD】修改：mapper.updateById()（主键 id 必填，自动刷新 update_time）
    public void update(SysUser user) {
        SysUser exist = userMapper.selectById(user.getId());
        if (exist == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        // 密码不在这里改，设为null防止被改掉
        user.setPassword(null);
        userMapper.updateById(user);
    }

    /**
     * 删除用户（逻辑删除）
     */
    @Override
    // 【CRUD】删除：mapper.deleteById()（因 @TableLogic 实际是 UPDATE SET deleted=1）
    public void delete(Long id) {
        userMapper.deleteById(id);
    }

    /**
     * 修改密码
     * 需要输入旧密码验证
     */
    @Override
    // 【CRUD/业务】updatePassword()
    public void updatePassword(Long userId, PasswordDTO passwordDTO) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 先验证旧密码对不对
        String oldPwd = DigestUtils.md5DigestAsHex(passwordDTO.getOldPassword().getBytes(StandardCharsets.UTF_8));
        if (!oldPwd.equals(user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        // 设置新密码
        String newPwd = DigestUtils.md5DigestAsHex(passwordDTO.getNewPassword().getBytes(StandardCharsets.UTF_8));
        user.setPassword(newPwd);
        userMapper.updateById(user);
    }

    /**
     * 重置密码
     * 管理员用的，把密码重置成默认密码123456
     */
    @Override
    // 【CRUD/业务】resetPassword()
    public void resetPassword(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        String newPwd = DigestUtils.md5DigestAsHex(Constants.DEFAULT_PASSWORD.getBytes(StandardCharsets.UTF_8));
        user.setPassword(newPwd);
        userMapper.updateById(user);
    }

    /**
     * 给用户对象填充角色名称和机构名称
     * 因为用户表里只有id，页面上要显示名字
     */
    private void fillUserInfo(SysUser user) {
        // 填充角色名称
        if (user.getRoleId() != null) {
            SysRole role = roleMapper.selectById(user.getRoleId().longValue());
            if (role != null) {
                user.setRoleName(role.getRoleName());
            }
        }
        // 填充机构名称
        if (user.getInstitutionId() != null) {
            Institution inst = institutionMapper.selectById(user.getInstitutionId());
            if (inst != null) {
                user.setInstitutionName(inst.getName());
            }
        }
    }
}
