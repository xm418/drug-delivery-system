package com.medical.drugdelivery.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medical.drugdelivery.entity.DeliveryTask;
import org.apache.ibatis.annotations.Mapper;

// ============================================================
// 【CRUD Mapper 接口】DeliveryTaskMapper
// 作用：数据访问层，继承 MyBatis Plus BaseMapper<DeliveryTask>
// BaseMapper 内置 17 个自动生成的 CRUD 方法，无需手写 SQL：
//   insert(entity)           → INSERT INTO {table} (...) VALUES (...)
//   deleteById(id)           → UPDATE SET deleted=1 WHERE id=?（逻辑删）
//   updateById(entity)       → UPDATE {table} SET ... WHERE id=?
//   selectById(id)           → SELECT * FROM {table} WHERE id=? AND deleted=0
//   selectList(wrapper)      → SELECT * FROM {table} WHERE ... AND deleted=0
//   selectPage(page,wrapper) → SELECT COUNT(*) + SELECT ... LIMIT ?,?
// 框架看 Entity 的 @TableName / @TableId / @TableLogic 注解自动拼 SQL
// ============================================================


/**
 * 配送任务表的Mapper，对应delivery_task表
 * 继承MyBatis-Plus的BaseMapper，基本的增删改查都有了
 */
@Mapper
public interface DeliveryTaskMapper extends BaseMapper<DeliveryTask> {
}
