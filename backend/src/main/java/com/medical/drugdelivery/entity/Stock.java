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
// 【CRUD Entity】Stock
// 对应数据库表：@table_name
// MyBatis Plus 通过注解驱动 CRUD 自动生成：
//   @TableName("table")      → 告诉框架对应哪张表
//   @TableId(type=AUTO)      → 主键自增
//   @TableLogic              → 删除变 UPDATE SET deleted=1
//   @TableField(fill=...)    → 自动时间戳（MyMetaObjectHandler）
//   @Data（Lombok）          → 自动生成 getter/setter
// ============================================================

// ============================================================
// 对应数据库表：@stock
// MyBatis Plus 通过以下注解驱动 CRUD 自动生成：
//   @TableName("table_name")       → 告诉框架对应哪张表
//   @TableId(type=IdType.AUTO)     → 主键自增
//   @TableLogic  // ← 逻辑删除标记：deleteById() 变成 UPDATE SET deleted=1
//   @TableField(fill=...)           → 自动时间戳（MyMetaObjectHandler）
//   @TableField(exist=false)        → 表里没有，页面显示用的扩展字段
//   @Data（Lombok）                 → 自动生成 getter/setter
// ============================================================


/**
 * 库存表实体类
 * 对应数据库表：stock
 * 主要用来存各个医疗机构的药品库存信息，包括数量、价格、批号、保质期啥的
 */
@Data
@TableName("stock")
public class Stock {

    /**
     * 主键ID，自增的，唯一标识一条库存记录
     */
    @TableId(type = IdType.AUTO)  // ← 主键自增（对应 MySQL AUTO_INCREMENT）
    private Long id;

    /**
     * 药品ID，关联到药品表，说明这是哪种药的库存
     */
    private Long drugId;

    /**
     * 医疗机构ID，说明这个库存属于哪家医院/卫生院
     */
    private Long institutionId;

    /**
     * 库存数量，就是还剩多少盒/多少瓶药
     */
    private Integer quantity;

    /**
     * 单价，每单位药品的价格，用BigDecimal防止精度丢失
     */
    private BigDecimal unitPrice;

    /**
     * 批号，药品的生产批号，用来追溯的
     */
    private String batchNumber;

    /**
     * 生产日期，这批药是什么时候生产的
     */
    private LocalDateTime productionDate;

    /**
     * 过期日期，这批药什么时候过期，快过期的要提醒
     */
    private LocalDateTime expiryDate;

    /**
     * 创建时间，这条记录什么时候加进来的，自动填充
     */
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间，这条记录最后修改是什么时候，自动填充
     */
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记，0表示没删，1表示删了，MyBatis-Plus的@TableLogic  // ← 逻辑删除标记：deleteById() 变成 UPDATE SET deleted=1
     */
    @TableLogic  // ← 逻辑删除标记：deleteById() 变成 UPDATE SET deleted=1
    private Integer deleted;

    // ========== 以下是数据库里没有的字段，查询的时候手动拼的 ==========

    /**
     * 药品名称，非数据库字段，联表查出来显示用的
     */
    @TableField(exist = false)  // ← 表里没有此字段，Service 查询后手动填充（页面显示用）
    private String drugName;

    /**
     * 药品编码，非数据库字段，药品的编号，方便查找
     */
    @TableField(exist = false)  // ← 表里没有此字段，Service 查询后手动填充（页面显示用）
    private String drugCode;

    /**
     * 机构名称，非数据库字段，就是医院/卫生院的名字，显示的时候用
     */
    @TableField(exist = false)  // ← 表里没有此字段，Service 查询后手动填充（页面显示用）
    private String institutionName;

    /**
     * 药品规格，非数据库字段，比如"0.5g*12片"这种描述
     */
    @TableField(exist = false)  // ← 表里没有此字段，Service 查询后手动填充（页面显示用）
    private String specification;

    /**
     * 计量单位，非数据库字段，比如盒、瓶、片之类的
     */
    @TableField(exist = false)  // ← 表里没有此字段，Service 查询后手动填充（页面显示用）
    private String unit;

    /**
     * 预警阈值，非数据库字段，库存低于这个数就要提醒补货了
     */
    @TableField(exist = false)  // ← 表里没有此字段，Service 查询后手动填充（页面显示用）
    private Integer warningThreshold;
}
