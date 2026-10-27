package com.medical.drugdelivery.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

// ============================================================
// 【CRUD Entity】DrugCategory
// 对应数据库表：@table_name
// MyBatis Plus 通过注解驱动 CRUD 自动生成：
//   @TableName("table")      → 告诉框架对应哪张表
//   @TableId(type=AUTO)      → 主键自增
//   @TableLogic              → 删除变 UPDATE SET deleted=1
//   @TableField(fill=...)    → 自动时间戳（MyMetaObjectHandler）
//   @Data（Lombok）          → 自动生成 getter/setter
// ============================================================

// ============================================================
// 对应数据库表：@drug_category
// MyBatis Plus 通过以下注解驱动 CRUD 自动生成：
//   @TableName("table_name")       → 告诉框架对应哪张表
//   @TableId(type=IdType.AUTO)     → 主键自增
//   @TableLogic  // ← 逻辑删除标记：deleteById() 变成 UPDATE SET deleted=1
//   @TableField(fill=...)           → 自动时间戳（MyMetaObjectHandler）
//   @TableField(exist=false)        → 表里没有，页面显示用的扩展字段
//   @Data（Lombok）                 → 自动生成 getter/setter
// ============================================================


/**
 * 药品分类实体类
 * 对应数据库表：drug_category
 * 药品的分类信息，支持多级分类，比如"抗生素"下面又分"青霉素类"、"头孢类"啥的
 */
@Data
@TableName("drug_category")
public class DrugCategory {

    /**
     * 主键ID，自增的，每个分类唯一标识
     */
    @TableId(type = IdType.AUTO)  // ← 主键自增（对应 MySQL AUTO_INCREMENT）
    private Long id;

    /**
     * 分类名称，比如"解热镇痛药"、"抗生素"、"心血管用药"这种
     */
    private String categoryName;

    /**
     * 分类编码，分类的唯一编号，方便系统识别用
     */
    private String categoryCode;

    /**
     * 父分类ID，顶级分类的话这个值是0或者null
     * 用来实现多级分类树结构的
     */
    private Long parentId;

    /**
     * 排序号，数字越小排越前面
     * 前端展示分类列表的时候按这个字段排序
     */
    private Integer sort;

    /**
     * 备注，补充说明用的，随便写啥都行
     */
    private String remark;

    /**
     * 分类状态
     * 0=停用（这个分类暂时不用了）
     * 1=正常使用中
     */
    private Integer status;

    /**
     * 创建时间，这条记录啥时候添加的，自动填充不用手动设置
     */
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间，最后一次修改的时间，自动填充不用手动设置
     */
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记
     * 0=未删除（正常数据）
     * 1=已删除（假删除，数据还在库里，只是查询的时候过滤掉了）
     * mybatis-plus的@TableLogic  // ← 逻辑删除标记：deleteById() 变成 UPDATE SET deleted=1
     */
    @TableLogic  // ← 逻辑删除标记：deleteById() 变成 UPDATE SET deleted=1
    private Integer deleted;

    // ==================== 非数据库字段（查询时额外封装的） ====================

    /**
     * 父分类名称，数据库里没有这个字段哈
     * 是自己联表查出来塞进去的，方便前端直接显示父分类名字
     */
    @TableField(exist = false)  // ← 表里没有此字段，Service 查询后手动填充（页面显示用）
    private String parentName;
}
