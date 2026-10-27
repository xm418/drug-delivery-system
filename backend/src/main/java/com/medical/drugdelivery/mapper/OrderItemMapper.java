package com.medical.drugdelivery.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medical.drugdelivery.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

// ============================================================
// 【CRUD Mapper 接口】OrderItemMapper
// 作用：数据访问层，继承 MyBatis Plus BaseMapper<OrderItem>
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
 * 订单明细表的Mapper，对应order_item表
 * 一个订单里的每一种药就是这里的一条记录
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}
