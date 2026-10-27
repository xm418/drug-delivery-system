package com.medical.drugdelivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.drugdelivery.common.PageResult;
import com.medical.drugdelivery.dto.PageQueryDTO;
import com.medical.drugdelivery.entity.DrugCategory;
import com.medical.drugdelivery.mapper.DrugCategoryMapper;
import com.medical.drugdelivery.service.DrugCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// ============================================================
// 【CRUD Service 实现】DrugCategoryServiceImpl
// 作用：业务逻辑层，调用 Mapper（继承 BaseMapper）执行数据库操作
//   add()    → 新增：补默认值 → mapper.insert()
//   page()   → 分页查询：构建 LambdaQueryWrapper 条件 → mapper.selectPage()
//   getById()→ 按 id 查详情：mapper.selectById() → 补关联表名称
//   update() → 修改：mapper.updateById()（主键 id 必填）
//   delete() → 删除：mapper.deleteById()（因 @TableLogic 实际是 UPDATE SET deleted=1）
// 事务：@Transactional 保证双表写入（如订单主表+明细）原子性
// ============================================================


/**
 * 药品分类服务实现类
 * 处理药品分类相关的业务逻辑
 * TODO: 药品分类的增删改查，后面可能要加树形结构返回
 */
@Service
public class DrugCategoryServiceImpl implements DrugCategoryService {

    // 自动注入药品分类Mapper
    @Autowired
    private DrugCategoryMapper categoryMapper;

    /**
     * 查询所有正常状态的药品分类列表
     * 按排序号和id升序排列，填充父级分类名称
     * @return 分类列表
     */
    @Override
    public List<DrugCategory> listAll() {
        // 创建查询条件包装器
        LambdaQueryWrapper<DrugCategory> wrapper = new LambdaQueryWrapper<>();
        // 状态为1表示正常
        wrapper.eq(DrugCategory::getStatus, 1);
        // 先按sort排序，再按id排序
        wrapper.orderByAsc(DrugCategory::getSort, DrugCategory::getId);
        // 执行查询
        List<DrugCategory> list = categoryMapper.selectList(wrapper);
        // 遍历填充父级分类名称
        // 用普通for循环，不用lambda的forEach，老师说这样好理解
        for (int i = 0; i < list.size(); i++) {
            fillParentName(list.get(i));
        }
        return list;
    }

    /**
     * 根据父级id查询子分类列表
     * 比如查"感冒药"下面的子分类
     * @param parentId 父级分类id
     * @return 子分类列表
     */
    @Override
    public List<DrugCategory> listByParentId(Long parentId) {
        LambdaQueryWrapper<DrugCategory> wrapper = new LambdaQueryWrapper<>();
        // 正常状态
        wrapper.eq(DrugCategory::getStatus, 1);
        // 父级id匹配
        wrapper.eq(DrugCategory::getParentId, parentId);
        // 按排序号和id升序
        wrapper.orderByAsc(DrugCategory::getSort, DrugCategory::getId);
        return categoryMapper.selectList(wrapper);
    }

    /**
     * 分页查询药品分类
     * 支持按分类名称关键字搜索
     * TODO: 后面可能要加按层级查询的功能
     * @param queryDTO 分页查询参数
     * @return 分页结果
     */
    @Override
    public PageResult<DrugCategory> page(PageQueryDTO queryDTO) {
        // 创建分页对象
        Page<DrugCategory> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<DrugCategory> wrapper = new LambdaQueryWrapper<>();

        // 关键字不为空就模糊查询分类名称
        // 用普通方式判断字符串非空，不用StringUtils
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().equals("")) {
            wrapper.like(DrugCategory::getCategoryName, queryDTO.getKeyword());
        }
        // 按创建时间倒序
        wrapper.orderByDesc(DrugCategory::getCreateTime);

        // 执行分页查询
        Page<DrugCategory> result = categoryMapper.selectPage(page, wrapper);
        // 遍历每条记录，填充父级分类名称
        // 普通for循环方式
        List<DrugCategory> records = result.getRecords();
        for (int i = 0; i < records.size(); i++) {
            fillParentName(records.get(i));
        }

        // 封装返回
        return PageResult.of(result.getTotal(), result.getRecords(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    /**
     * 根据id查询药品分类详情
     * 同时填充父级分类名称
     * @param id 分类id
     * @return 分类对象
     */
    @Override
    public DrugCategory getById(Long id) {
        // 先查出来
        DrugCategory category = categoryMapper.selectById(id);
        // 不为空就填充父级名称
        if (category != null) {
            fillParentName(category);
        }
        return category;
    }

    /**
     * 新增药品分类
     * 如果状态和排序没设置，给默认值
     * TODO: 要不要判断父分类是否存在？以后再加吧
     * @param category 分类信息
     */
    @Override
    // 【CRUD】新增：补默认值 → mapper.insert()（MyBatis Plus 自动生成 INSERT）
    public void add(DrugCategory category) {
        // 状态默认为1（正常）
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        // 排序号默认为0
        if (category.getSort() == null) {
            category.setSort(0);
        }
        // 插入数据库
        categoryMapper.insert(category);
    }

    /**
     * 更新药品分类信息
     * 根据id更新
     * @param category 分类信息
     */
    @Override
    // 【CRUD】修改：mapper.updateById()（主键 id 必填，自动刷新 update_time）
    public void update(DrugCategory category) {
        categoryMapper.updateById(category);
    }

    /**
     * 删除药品分类
     * 根据id删除
     * TODO: 删除前要判断下面有没有子分类吧？不然删了父的子的就成孤儿了
     * @param id 分类id
     */
    @Override
    // 【CRUD】删除：mapper.deleteById()（因 @TableLogic 实际是 UPDATE SET deleted=1）
    public void delete(Long id) {
        categoryMapper.deleteById(id);
    }

    /**
     * 填充父级分类名称
     * 私有方法，内部调用
     * 根据parentId查父分类，设置parentName
     * TODO: 这里N+1问题，数据多了会慢，以后优化成一次查所有父分类
     * @param category 分类对象
     */
    private void fillParentName(DrugCategory category) {
        // 有父级id且大于0才查
        if (category.getParentId() != null && category.getParentId() > 0) {
            // 根据父id查父分类
            DrugCategory parent = categoryMapper.selectById(category.getParentId());
            // 查到了就设上父分类名称
            if (parent != null) {
                category.setParentName(parent.getCategoryName());
            }
        }
    }
}
