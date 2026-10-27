package com.medical.drugdelivery.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// ============================================================
// 【CRUD Entity】OrderItem
// 对应数据库表：@table_name
// MyBatis Plus 通过注解驱动 CRUD 自动生成：
//   @TableName("table")      → 告诉框架对应哪张表
//   @TableId(type=AUTO)      → 主键自增
//   @TableLogic              → 删除变 UPDATE SET deleted=1
//   @TableField(fill=...)    → 自动时间戳（MyMetaObjectHandler）
//   @Data（Lombok）          → 自动生成 getter/setter
// ============================================================

// ============================================================
// 对应数据库表：@order_item
// MyBatis Plus 通过以下注解驱动 CRUD 自动生成：
//   @TableName("table_name")       → 告诉框架对应哪张表
//   @TableId(type=IdType.AUTO)     → 主键自增
//   @TableLogic  // ← 逻辑删除标记：deleteById() 变成 UPDATE SET deleted=1
//   @TableField(fill=...)           → 自动时间戳（MyMetaObjectHandler）
//   @TableField(exist=false)        → 表里没有，页面显示用的扩展字段
//   @Data（Lombok）                 → 自动生成 getter/setter
// ============================================================


/**
 * 订单明细表实体类
 * 对应数据库表：order_item
 * 一个订单里包含好几种药品，每种药的明细就在这张表里，买了啥、多少、多少钱都记着
 */
@Data
@TableName("order_item")
public class OrderItem {

    /**
     * 主键ID，自增的，唯一标识一条订单明细
     */
    @TableId(type = IdType.AUTO)  // ← 主键自增（对应 MySQL AUTO_INCREMENT）
    private Long id;

    /**
     * 订单ID，这条明细属于哪个订单的，关联订单表
     */
    private Long orderId;

    /**
     * 药品ID，买的是哪种药，关联药品表
     */
    private Long drugId;

    /**
     * 药品名称，直接存下来，免得以后药品改名了历史订单也变
     */
    private String drugName;

    /**
     * 药品规格，比如"0.5g*12片/盒"，也是冗余存的，防止以后规格改了
     */
    private String specification;

    /**
     * 计量单位，比如盒、瓶、支之类的
     */
    private String unit;

    /**
     * 购买数量，买了多少单位的药
     */
    private Integer quantity;

    /**
     * 单价，每单位药品的价格，用BigDecimal更精确，不会有浮点误差
     */
    private BigDecimal unitPrice;

    /**
     * 小计，就是单价*数量，这一行总共多少钱
     */
    private BigDecimal totalPrice;

    /**
     * 创建时间，这条明细什么时候加的，自动填充
     */
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间，最后修改时间，自动填充
     */
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记，0表示正常，1表示删了
     */
    @TableLogic  // ← 逻辑删除标记：deleteById() 变成 UPDATE SET deleted=1
    private Integer deleted;
}
