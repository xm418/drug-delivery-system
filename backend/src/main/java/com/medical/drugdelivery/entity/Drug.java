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
// 【CRUD Entity】Drug
// 对应数据库表：@table_name
// MyBatis Plus 通过注解驱动 CRUD 自动生成：
//   @TableName("table")      → 告诉框架对应哪张表
//   @TableId(type=AUTO)      → 主键自增
//   @TableLogic              → 删除变 UPDATE SET deleted=1
//   @TableField(fill=...)    → 自动时间戳（MyMetaObjectHandler）
//   @Data（Lombok）          → 自动生成 getter/setter
// ============================================================

// ============================================================
// 对应数据库表：@drug
// MyBatis Plus 通过以下注解驱动 CRUD 自动生成：
//   @TableName("table_name")       → 告诉框架对应哪张表
//   @TableId(type=IdType.AUTO)     → 主键自增
//   @TableLogic  // ← 逻辑删除标记：deleteById() 变成 UPDATE SET deleted=1
//   @TableField(fill=...)           → 自动时间戳（MyMetaObjectHandler）
//   @TableField(exist=false)        → 表里没有，页面显示用的扩展字段
//   @Data（Lombok）                 → 自动生成 getter/setter
// ============================================================


/**
 * 药品信息实体类
 * 对应数据库表：drug
 */
@Data
@TableName("drug")
public class Drug {

    /**
     * 药品ID（主键，自增）
     */
    @TableId(type = IdType.AUTO)  // ← 主键自增（对应 MySQL AUTO_INCREMENT）
    private Long id;

    /**
     * 药品名称
     */
    private String drugName;

    /**
     * 药品编码（唯一）
     */
    private String drugCode;

    /**
     * 分类ID
     * 关联 drug_category 表
     */
    private Long categoryId;

    /**
     * 药品规格，比如：0.5g*12片、100ml/瓶
     */
    private String specification;

    /**
     * 生产厂家
     */
    private String manufacturer;

    /**
     * 批准文号（国药准字）
     */
    private String approvalNumber;

    /**
     * 单价
     * 用BigDecimal防止精度丢失
     */
    private BigDecimal price;

    /**
     * 单位，比如：盒、瓶、片、支
     */
    private String unit;

    /**
     * 状态：1-上架 0-下架
     */
    private Integer status;

    /**
     * 药品描述/说明
     */
    private String description;

    /**
     * 药品图片地址
     */
    private String image;

    /**
     * 库存预警阈值
     * 库存低于这个数就预警
     */
    private Integer warningThreshold;

    /**
     * 创建时间
     * 自动填充，新增时设置
     */
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     * 自动填充，新增和修改时设置
     */
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标志：0-正常 1-已删除
     * MyBatis Plus的@TableLogic  // ← 逻辑删除标记：deleteById() 变成 UPDATE SET deleted=1
     */
    @TableLogic  // ← 逻辑删除标记：deleteById() 变成 UPDATE SET deleted=1
    private Integer deleted;

    // ========== 以下是表中没有的字段，用于页面显示 ==========

    /**
     * 分类名称
     * 联表查询的时候填进去的
     */
    @TableField(exist = false)  // ← 表里没有此字段，Service 查询后手动填充（页面显示用）
    private String categoryName;
}
