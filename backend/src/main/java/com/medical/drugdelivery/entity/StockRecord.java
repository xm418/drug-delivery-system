package com.medical.drugdelivery.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

// ============================================================
// 【CRUD Entity】StockRecord
// 对应数据库表：@table_name
// MyBatis Plus 通过注解驱动 CRUD 自动生成：
//   @TableName("table")      → 告诉框架对应哪张表
//   @TableId(type=AUTO)      → 主键自增
//   @TableLogic              → 删除变 UPDATE SET deleted=1
//   @TableField(fill=...)    → 自动时间戳（MyMetaObjectHandler）
//   @Data（Lombok）          → 自动生成 getter/setter
// ============================================================

// ============================================================
// 对应数据库表：@stock_record
// MyBatis Plus 通过以下注解驱动 CRUD 自动生成：
//   @TableName("table_name")       → 告诉框架对应哪张表
//   @TableId(type=IdType.AUTO)     → 主键自增
//   @TableLogic  // ← 逻辑删除标记：deleteById() 变成 UPDATE SET deleted=1
//   @TableField(fill=...)           → 自动时间戳（MyMetaObjectHandler）
//   @TableField(exist=false)        → 表里没有，页面显示用的扩展字段
//   @Data（Lombok）                 → 自动生成 getter/setter
// ============================================================


/**
 * 库存记录表实体类
 * 对应数据库表：stock_record
 * 库存变动的流水账，每次入库、出库、调拨啥的都在这留一条记录，方便审计和查账
 */
@Data
@TableName("stock_record")
public class StockRecord {

    /**
     * 主键ID，自增的，唯一标识一条库存变动记录
     */
    @TableId(type = IdType.AUTO)  // ← 主键自增（对应 MySQL AUTO_INCREMENT）
    private Long id;

    /**
     * 药品ID，变动的是哪种药，关联药品表
     */
    private Long drugId;

    /**
     * 医疗机构ID，哪家机构的库存变了
     */
    private Long institutionId;

    /**
     * 操作类型，是入库还是出库还是其他操作
     * 枚举值大概是：1-入库，2-出库，3-调拨入库，4-调拨出库，5-盘点调整
     */
    private Integer operationType;

    /**
     * 变动数量，就是这次操作了多少数量的药，正数表示增加，负数表示减少
     */
    private Integer quantity;

    /**
     * 变动前数量，操作之前库存有多少
     */
    private Integer beforeQuantity;

    /**
     * 变动后数量，操作之后库存剩多少
     */
    private Integer afterQuantity;

    /**
     * 批号，涉及到哪一批药的变动
     */
    private String batchNumber;

    /**
     * 操作人ID，谁干的这件事，关联用户表
     */
    private Long operatorId;

    /**
     * 操作人姓名，直接存名字，省得联表查
     */
    private String operatorName;

    /**
     * 备注，有啥特殊说明都写在这，比如为啥调整库存之类的
     */
    private String remark;

    /**
     * 关联订单号，如果这次变动是因为某个订单产生的，就把订单号记下来方便追溯
     */
    private String relatedOrderNo;

    /**
     * 创建时间，这条记录什么时候生成的，自动填充（库存变动记录一般不改，所以只有createTime）
     */
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 逻辑删除标记，0表示正常，1表示删了（库存记录一般不删，但还是留个字段备用）
     */
    @TableLogic  // ← 逻辑删除标记：deleteById() 变成 UPDATE SET deleted=1
    private Integer deleted;

    // ========== 以下是数据库里没有的字段，前端展示用的 ==========

    /**
     * 药品名称，非数据库字段，联表查出来显示用的
     */
    @TableField(exist = false)  // ← 表里没有此字段，Service 查询后手动填充（页面显示用）
    private String drugName;

    /**
     * 机构名称，非数据库字段，医疗机构的名字，显示的时候用
     */
    @TableField(exist = false)  // ← 表里没有此字段，Service 查询后手动填充（页面显示用）
    private String institutionName;
}
