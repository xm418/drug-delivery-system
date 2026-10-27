package com.medical.drugdelivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.drugdelivery.common.Constants;
import com.medical.drugdelivery.common.PageResult;
import com.medical.drugdelivery.dto.PageQueryDTO;
import com.medical.drugdelivery.entity.SysRole;
import com.medical.drugdelivery.mapper.SysRoleMapper;
import com.medical.drugdelivery.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// ============================================================
// 【CRUD Service 实现】RoleServiceImpl
// 作用：业务逻辑层，调用 Mapper（继承 BaseMapper）执行数据库操作
//   add()    → 新增：补默认值 → mapper.insert()
//   page()   → 分页查询：构建 LambdaQueryWrapper 条件 → mapper.selectPage()
//   getById()→ 按 id 查详情：mapper.selectById() → 补关联表名称
//   update() → 修改：mapper.updateById()（主键 id 必填）
//   delete() → 删除：mapper.deleteById()（因 @TableLogic 实际是 UPDATE SET deleted=1）
// 事务：@Transactional 保证双表写入（如订单主表+明细）原子性
// ============================================================


/**
 * 角色服务实现类
 * 处理系统角色相关的业务逻辑
 * TODO: 角色管理的增删改查，后面还要加权限关联
 */
@Service
public class RoleServiceImpl implements RoleService {

    // 自动注入角色Mapper
    @Autowired
    private SysRoleMapper roleMapper;

    /**
     * 查询所有正常状态的角色列表
     * 按id升序排列
     * @return 角色列表
     */
    @Override
    public List<SysRole> listAll() {
        // 创建查询条件
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        // 只查正常状态的角色
        wrapper.eq(SysRole::getStatus, Constants.ROLE_STATUS_NORMAL);
        // 按id升序排序
        wrapper.orderByAsc(SysRole::getId);
        // 执行查询返回结果
        return roleMapper.selectList(wrapper);
    }

    /**
     * 分页查询角色列表
     * 支持按角色名称关键字搜索
     * TODO: 后面可能要加角色编码、状态等查询条件
     * @param queryDTO 分页查询参数
     * @return 分页结果
     */
    @Override
    public PageResult<SysRole> page(PageQueryDTO queryDTO) {
        // 创建分页对象，当前页和每页大小
        Page<SysRole> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();

        // 关键字不为空的话，按角色名称模糊查询
        // 不用StringUtils.hasText，用最基础的方式判断
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().equals("")) {
            wrapper.like(SysRole::getRoleName, queryDTO.getKeyword());
        }
        // 按创建时间倒序，最新的在前面
        wrapper.orderByDesc(SysRole::getCreateTime);

        // 执行分页查询
        Page<SysRole> result = roleMapper.selectPage(page, wrapper);
        // 封装成PageResult返回
        return PageResult.of(result.getTotal(), result.getRecords(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    /**
     * 根据id查询角色详情
     * @param id 角色id
     * @return 角色对象
     */
    @Override
    public SysRole getById(Long id) {
        // 直接根据id查询
        return roleMapper.selectById(id);
    }

    /**
     * 新增角色
     * 如果状态没设置，默认设为正常状态
     * TODO: 要不要加角色名称重复校验？后面再说吧
     * @param role 角色信息
     */
    @Override
    // 【CRUD】新增：补默认值 → mapper.insert()（MyBatis Plus 自动生成 INSERT）
    public void add(SysRole role) {
        // 状态为空的话默认设为正常
        if (role.getStatus() == null) {
            role.setStatus(Constants.ROLE_STATUS_NORMAL);
        }
        // 插入数据库
        roleMapper.insert(role);
    }

    /**
     * 更新角色信息
     * 根据id更新
     * TODO: 更新的时候要不要同步更新关联的权限？以后再说
     * @param role 角色信息
     */
    @Override
    // 【CRUD】修改：mapper.updateById()（主键 id 必填，自动刷新 update_time）
    public void update(SysRole role) {
        roleMapper.updateById(role);
    }

    /**
     * 删除角色
     * 根据id删除
     * TODO: 删除前要判断有没有用户在使用这个角色吧？不然用户就没角色了
     * TODO: 还有角色关联的权限也要一起删掉
     * @param id 角色id
     */
    @Override
    // 【CRUD】删除：mapper.deleteById()（因 @TableLogic 实际是 UPDATE SET deleted=1）
    public void delete(Long id) {
        roleMapper.deleteById(id);
    }
}
