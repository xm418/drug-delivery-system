package com.medical.drugdelivery.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

// ============================================================
// 【CRUD Entity】SysRole
// 对应数据库表：@table_name
// MyBatis Plus 通过注解驱动 CRUD 自动生成：
//   @TableName("table")      → 告诉框架对应哪张表
//   @TableId(type=AUTO)      → 主键自增
//   @TableLogic              → 删除变 UPDATE SET deleted=1
//   @TableField(fill=...)    → 自动时间戳（MyMetaObjectHandler）
//   @Data（Lombok）          → 自动生成 getter/setter
// ============================================================

// ============================================================
// 对应数据库表：@sys_role
// MyBatis Plus 通过以下注解驱动 CRUD 自动生成：
//   @TableName("table_name")       → 告诉框架对应哪张表
//   @TableId(type=IdType.AUTO)     → 主键自增
//   @TableLogic  // ← 逻辑删除标记：deleteById() 变成 UPDATE SET deleted=1
//   @TableField(fill=...)           → 自动时间戳（MyMetaObjectHandler）
//   @TableField(exist=false)        → 表里没有，页面显示用的扩展字段
//   @Data（Lombok）                 → 自动生成 getter/setter
// ============================================================


/**
 * 系统角色实体类
 * 对应数据库表：sys_role
 * 系统里的角色信息，比如管理员、药剂师、配送员啥的，用户通过角色来分配权限
 */
@Data
@TableName("sys_role")
public class SysRole {

    /**
     * 主键ID，自增的，每个角色唯一标识
     */
    @TableId(type = IdType.AUTO)  // ← 主键自增（对应 MySQL AUTO_INCREMENT）
    private Long id;

    /**
     * 角色名称，比如"系统管理员"、"药剂科主任"、"配送员"、"临床医生"这种
     */
    private String roleName;

    /**
     * 角色编码，角色的唯一英文标识，比如"admin"、"pharmacist"、"delivery"
     * 权限判断的时候一般用这个编码，比用名字靠谱
     */
    private String roleCode;

    /**
     * 角色描述，简单说明一下这个角色是干啥的，有啥权限
     */
    private String description;

    /**
     * 角色状态
     * 0=停用（这个角色暂时不能用了，分配了这个角色的用户也用不了）
     * 1=正常使用中
     */
    private Integer status;

    /**
     * 创建时间，这条记录啥时候添加的，自动填充不用管
     */
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间，最后一次修改的时间，自动填充不用管
     */
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记
     * 0=未删除（正常数据）
     * 1=已删除（软删除，数据还在库里，只是查询时被过滤掉了）
     * 用了mybatis-plus的@TableLogic  // ← 逻辑删除标记：deleteById() 变成 UPDATE SET deleted=1
     */
    @TableLogic  // ← 逻辑删除标记：deleteById() 变成 UPDATE SET deleted=1
    private Integer deleted;
}
