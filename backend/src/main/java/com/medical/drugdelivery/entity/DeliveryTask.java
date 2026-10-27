package com.medical.drugdelivery.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

// ============================================================
// 【CRUD Entity】DeliveryTask
// 对应数据库表：@table_name
// MyBatis Plus 通过注解驱动 CRUD 自动生成：
//   @TableName("table")      → 告诉框架对应哪张表
//   @TableId(type=AUTO)      → 主键自增
//   @TableLogic              → 删除变 UPDATE SET deleted=1
//   @TableField(fill=...)    → 自动时间戳（MyMetaObjectHandler）
//   @Data（Lombok）          → 自动生成 getter/setter
// ============================================================

// ============================================================
// 对应数据库表：@delivery_task
// MyBatis Plus 通过以下注解驱动 CRUD 自动生成：
//   @TableName("table_name")       → 告诉框架对应哪张表
//   @TableId(type=IdType.AUTO)     → 主键自增
//   @TableLogic  // ← 逻辑删除标记：deleteById() 变成 UPDATE SET deleted=1
//   @TableField(fill=...)           → 自动时间戳（MyMetaObjectHandler）
//   @TableField(exist=false)        → 表里没有，页面显示用的扩展字段
//   @Data（Lombok）                 → 自动生成 getter/setter
// ============================================================


/**
 * 配送任务表实体类
 * 对应数据库表：delivery_task
 * 药品从一家机构配送到另一家机构的任务记录，谁送、送到哪、状态咋样都在这
 */
@Data
@TableName("delivery_task")
public class DeliveryTask {

    /**
     * 主键ID，自增的，唯一标识一条配送任务
     */
    @TableId(type = IdType.AUTO)  // ← 主键自增（对应 MySQL AUTO_INCREMENT）
    private Long id;

    /**
     * 任务编号，配送任务的单号，类似"PS20240101001"这种格式
     */
    private String taskNo;

    /**
     * 关联订单ID，这个配送任务是对应哪个订单的
     */
    private Long orderId;

    /**
     * 发货机构ID，从哪家医院/卫生院发出去的
     */
    private Long fromInstitutionId;

    /**
     * 收货机构ID，要送到哪家医院/卫生院去
     */
    private Long toInstitutionId;

    /**
     * 配送员ID，负责送这批药的人是谁
     */
    private Long deliveryPersonId;

    /**
     * 配送员姓名，直接存名字，省得联表查了
     */
    private String deliveryPersonName;

    /**
     * 配送员电话，联系配送员用的
     */
    private String deliveryPhone;

    /**
     * 任务状态，配送进行到哪一步了
     * 枚举值大概是：1-待发货，2-配送中，3-已送达，4-已取消
     */
    private Integer status;

    /**
     * 发货时间，什么时候从发货机构出发的
     */
    private LocalDateTime deliveryTime;

    /**
     * 收货时间，收货机构什么时候收到的
     */
    private LocalDateTime receiveTime;

    /**
     * 备注，有啥特殊情况都写在这
     */
    private String remark;

    /**
     * 创建时间，这条任务什么时候建的，自动填充
     */
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间，最后修改时间，自动填充
     */
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记，0表示没删，1表示删了
     */
    @TableLogic  // ← 逻辑删除标记：deleteById() 变成 UPDATE SET deleted=1
    private Integer deleted;

    // ========== 以下是数据库里没有的字段，前端展示用的 ==========

    /**
     * 订单编号，非数据库字段，对应订单的单号，显示的时候用
     */
    @TableField(exist = false)  // ← 表里没有此字段，Service 查询后手动填充（页面显示用）
    private String orderNo;

    /**
     * 发货机构名称，非数据库字段，就是发货医院/卫生院的名字
     */
    @TableField(exist = false)  // ← 表里没有此字段，Service 查询后手动填充（页面显示用）
    private String fromInstitutionName;

    /**
     * 收货机构名称，非数据库字段，就是收货医院/卫生院的名字
     */
    @TableField(exist = false)  // ← 表里没有此字段，Service 查询后手动填充（页面显示用）
    private String toInstitutionName;
}
