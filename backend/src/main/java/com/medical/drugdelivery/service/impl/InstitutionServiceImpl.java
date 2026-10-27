package com.medical.drugdelivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.drugdelivery.common.Constants;
import com.medical.drugdelivery.common.PageResult;
import com.medical.drugdelivery.dto.PageQueryDTO;
import com.medical.drugdelivery.entity.Institution;
import com.medical.drugdelivery.mapper.InstitutionMapper;
import com.medical.drugdelivery.service.InstitutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// ============================================================
// 【CRUD Service 实现】InstitutionServiceImpl
// 作用：业务逻辑层，调用 Mapper（继承 BaseMapper）执行数据库操作
//   add()    → 新增：补默认值 → mapper.insert()
//   page()   → 分页查询：构建 LambdaQueryWrapper 条件 → mapper.selectPage()
//   getById()→ 按 id 查详情：mapper.selectById() → 补关联表名称
//   update() → 修改：mapper.updateById()（主键 id 必填）
//   delete() → 删除：mapper.deleteById()（因 @TableLogic 实际是 UPDATE SET deleted=1）
// 事务：@Transactional 保证双表写入（如订单主表+明细）原子性
// ============================================================


/**
 * 医疗机构服务实现类
 * 处理医疗机构相关的业务逻辑
 * TODO: 以后可能需要加缓存，现在先这样写吧
 */
@Service
public class InstitutionServiceImpl implements InstitutionService {

    // 自动注入机构Mapper
    @Autowired
    private InstitutionMapper institutionMapper;

    /**
     * 查询所有正常状态的机构列表
     * 按级别和id升序排列，并且填充父级机构名称
     * @return 机构列表
     */
    @Override
    public List<Institution> listAll() {
        // 创建查询条件包装器
        LambdaQueryWrapper<Institution> wrapper = new LambdaQueryWrapper<>();
        // 只查状态正常的机构
        wrapper.eq(Institution::getStatus, Constants.INSTITUTION_STATUS_NORMAL);
        // 按级别升序，同级别按id升序
        wrapper.orderByAsc(Institution::getLevel, Institution::getId);
        // 执行查询
        List<Institution> list = institutionMapper.selectList(wrapper);
        // 遍历填充父级机构名称
        // TODO: 这里用普通for循环，老师说不要用forEach，容易看不懂
        for (int i = 0; i < list.size(); i++) {
            fillParentName(list.get(i));
        }
        return list;
    }

    /**
     * 根据级别查询机构列表
     * 比如查一级医院、二级医院之类的
     * @param level 机构级别
     * @return 该级别的机构列表
     */
    @Override
    public List<Institution> listByLevel(Integer level) {
        LambdaQueryWrapper<Institution> wrapper = new LambdaQueryWrapper<>();
        // 状态正常
        wrapper.eq(Institution::getStatus, Constants.INSTITUTION_STATUS_NORMAL);
        // 指定级别
        wrapper.eq(Institution::getLevel, level);
        // 按id排序
        wrapper.orderByAsc(Institution::getId);
        return institutionMapper.selectList(wrapper);
    }

    /**
     * 根据父级id查询子机构列表
     * 比如查某家医院下属的卫生室
     * @param parentId 父级机构id
     * @return 子机构列表
     */
    @Override
    public List<Institution> listByParentId(Long parentId) {
        LambdaQueryWrapper<Institution> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Institution::getStatus, Constants.INSTITUTION_STATUS_NORMAL);
        wrapper.eq(Institution::getParentId, parentId);
        wrapper.orderByAsc(Institution::getId);
        return institutionMapper.selectList(wrapper);
    }

    /**
     * 分页查询机构列表
     * 支持按名称关键字搜索和按级别过滤
     * TODO: 后面可能要加更多查询条件，比如地址啥的
     * @param queryDTO 分页查询参数
     * @param level 机构级别（可选）
     * @return 分页结果
     */
    @Override
    public PageResult<Institution> page(PageQueryDTO queryDTO, Integer level) {
        // 创建分页对象，传入当前页和每页大小
        Page<Institution> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<Institution> wrapper = new LambdaQueryWrapper<>();

        // 关键字不为空的话，就按名称模糊查询
        // 用最朴素的方式判断字符串非空
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().equals("")) {
            wrapper.like(Institution::getName, queryDTO.getKeyword());
        }
        // 级别不为空的话，按级别过滤
        if (level != null) {
            wrapper.eq(Institution::getLevel, level);
        }
        // 按创建时间倒序，最新的在前面
        wrapper.orderByDesc(Institution::getCreateTime);

        // 执行分页查询
        Page<Institution> result = institutionMapper.selectPage(page, wrapper);
        // 遍历每条记录，填充父级机构名称
        // 用普通for循环，不用forEach
        List<Institution> records = result.getRecords();
        for (int i = 0; i < records.size(); i++) {
            fillParentName(records.get(i));
        }

        // 封装成PageResult返回
        return PageResult.of(result.getTotal(), result.getRecords(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    /**
     * 根据id查询机构详情
     * 同时会填充父级机构名称
     * @param id 机构id
     * @return 机构对象
     */
    @Override
    public Institution getById(Long id) {
        // 先根据id查出来
        Institution institution = institutionMapper.selectById(id);
        // 查到了就填充父级名称
        if (institution != null) {
            fillParentName(institution);
        }
        return institution;
    }

    /**
     * 新增机构
     * 如果状态没设置，默认设为正常状态
     * TODO: 要不要加重复名称校验？后面再说吧
     * @param institution 机构信息
     */
    @Override
    // 【CRUD】新增：补默认值 → mapper.insert()（MyBatis Plus 自动生成 INSERT）
    public void add(Institution institution) {
        // 如果状态为空，默认设为正常
        if (institution.getStatus() == null) {
            institution.setStatus(Constants.INSTITUTION_STATUS_NORMAL);
        }
        // 插入数据库
        institutionMapper.insert(institution);
    }

    /**
     * 更新机构信息
     * 根据id更新
     * @param institution 机构信息
     */
    @Override
    // 【CRUD】修改：mapper.updateById()（主键 id 必填，自动刷新 update_time）
    public void update(Institution institution) {
        institutionMapper.updateById(institution);
    }

    /**
     * 删除机构
     * 根据id物理删除
     * TODO: 以后改成逻辑删除比较好，现在先这样
     * @param id 机构id
     */
    @Override
    // 【CRUD】删除：mapper.deleteById()（因 @TableLogic 实际是 UPDATE SET deleted=1）
    public void delete(Long id) {
        institutionMapper.deleteById(id);
    }

    /**
     * 填充父级机构名称
     * 私有方法，内部调用
     * 根据parentId查父机构，然后把名称设进去
     * TODO: 这里每次都查数据库，性能不太好，以后可以优化成批量查
     * @param institution 机构对象
     */
    private void fillParentName(Institution institution) {
        // 有父级id而且大于0才去查
        if (institution.getParentId() != null && institution.getParentId() > 0) {
            // 根据父id查父机构
            Institution parent = institutionMapper.selectById(institution.getParentId());
            // 查到了就把父机构名称设进去
            if (parent != null) {
                institution.setParentName(parent.getName());
            }
        }
    }
}
