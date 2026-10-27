package com.medical.drugdelivery.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// ============================================================
// 【CRUD Entity】DrugOrder
// 对应数据库表：@table_name
// MyBatis Plus 通过注解驱动 CRUD 自动生成：
//   @TableName("table")      → 告诉框架对应哪张表
//   @TableId(type=AUTO)      → 主键自增
//   @TableLogic              → 删除变 UPDATE SET deleted=1
//   @TableField(fill=...)    → 自动时间戳（MyMetaObjectHandler）
//   @Data（Lombok）          → 自动生成 getter/setter
// ============================================================

// ============================================================
// 对应数据库表：@drug_order
// MyBatis Plus 通过以下注解驱动 CRUD 自动生成：
//   @TableName("table_name")       → 告诉框架对应哪张表
//   @TableId(type=IdType.AUTO)     → 主键自增
//   @TableLogic  // ← 逻辑删除标记：deleteById() 变成 UPDATE SET deleted=1
//   @TableField(fill=...)           → 自动时间戳（MyMetaObjectHandler）
//   @TableField(exist=false)        → 表里没有，页面显示用的扩展字段
//   @Data（Lombok）                 → 自动生成 getter/setter
// ============================================================


/**
 * 订单实体类
 * 对应数据库表：drug_order
 * 订单主表，一个订单有多个订单项
 */
@Data
@TableName("drug_order")
public class DrugOrder {

    /**
     * 订单ID（主键，自增）
     */
    @TableId(type = IdType.AUTO)  // ← 主键自增（对应 MySQL AUTO_INCREMENT）
    private Long id;

    /**
     * 订单编号（唯一）
     * 格式：DD + 时间戳 + 随机数
     */
    private String orderNo;

    /**
     * 下单机构ID
     * 哪个医疗机构下的单
     */
    private Long institutionId;

    /**
     * 下单用户ID
     */
    private Long userId;

    /**
     * 订单状态
     * 0-待审核 1-已审核 2-配送中 3-已完成 4-已驳回 5-已取消
     */
    private Integer status;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 总数量
     */
    private Integer totalQuantity;

    /**
     * 审核人ID
     */
    private Long approveBy;

    /**
     * 审核时间
     */
    private LocalDateTime approveTime;

    /**
     * 审核意见/备注
     */
    private String approveRemark;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 创建时间（下单时间）
     */
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除
     */
    @TableLogic  // ← 逻辑删除标记：deleteById() 变成 UPDATE SET deleted=1
    private Integer deleted;

    // ========== 非数据库字段 ==========

    /**
     * 下单机构名称（查询时填充）
     */
    @TableField(exist = false)  // ← 表里没有此字段，Service 查询后手动填充（页面显示用）
    private String institutionName;

    /**
     * 下单人姓名（查询时填充）
     */
    @TableField(exist = false)  // ← 表里没有此字段，Service 查询后手动填充（页面显示用）
    private String username;

    /**
     * 审核人姓名（查询时填充）
     */
    @TableField(exist = false)  // ← 表里没有此字段，Service 查询后手动填充（页面显示用）
    private String approveByName;

    /**
     * 订单明细列表
     * 订单详情页用
     */
    @TableField(exist = false)  // ← 表里没有此字段，Service 查询后手动填充（页面显示用）
    private List<OrderItem> items;
}
