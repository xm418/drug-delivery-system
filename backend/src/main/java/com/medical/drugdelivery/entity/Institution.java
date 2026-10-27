package com.medical.drugdelivery.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

// ============================================================
// 【CRUD Entity】Institution
// 对应数据库表：@table_name
// MyBatis Plus 通过注解驱动 CRUD 自动生成：
//   @TableName("table")      → 告诉框架对应哪张表
//   @TableId(type=AUTO)      → 主键自增
//   @TableLogic              → 删除变 UPDATE SET deleted=1
//   @TableField(fill=...)    → 自动时间戳（MyMetaObjectHandler）
//   @Data（Lombok）          → 自动生成 getter/setter
// ============================================================

// ============================================================
// 对应数据库表：@institution
// MyBatis Plus 通过以下注解驱动 CRUD 自动生成：
//   @TableName("table_name")       → 告诉框架对应哪张表
//   @TableId(type=IdType.AUTO)     → 主键自增
//   @TableLogic  // ← 逻辑删除标记：deleteById() 变成 UPDATE SET deleted=1
//   @TableField(fill=...)           → 自动时间戳（MyMetaObjectHandler）
//   @TableField(exist=false)        → 表里没有，页面显示用的扩展字段
//   @Data（Lombok）                 → 自动生成 getter/setter
// ============================================================


/**
 * 医疗机构实体类
 * 对应数据库表：institution
 * 医共体里面的各个医院、卫生院啥的都存在这张表里，支持上下级层级关系
 */
@Data
@TableName("institution")
public class Institution {

    /**
     * 主键ID，自增的，每条记录唯一标识
     */
    @TableId(type = IdType.AUTO)  // ← 主键自增（对应 MySQL AUTO_INCREMENT）
    private Long id;

    /**
     * 机构名称，比如"XX县人民医院"、"XX镇卫生院"
     */
    private String name;

    /**
     * 机构编码，相当于机构的身份证号，唯一的，用来区分不同机构
     */
    private String code;

    /**
     * 机构级别
     * 1=县级医院（医共体牵头单位）
     * 2=乡镇卫生院/社区卫生服务中心
     * 3=村卫生室/社区卫生服务站
     * 数字越小级别越高哈
     */
    private Integer level;

    /**
     * 上级机构ID，顶级机构的话这个值是0或者null
     * 比如村卫生室的上级是乡镇卫生院，乡镇卫生院的上级是县医院
     */
    private Long parentId;

    /**
     * 机构地址，就是医院在哪条路多少号那种
     */
    private String address;

    /**
     * 联系人姓名，对接药品配送的那个人
     */
    private String contactPerson;

    /**
     * 联系电话，配送药品的时候打电话用的
     */
    private String contactPhone;

    /**
     * 机构状态
     * 0=停用（这个机构暂时不参与配送了）
     * 1=正常使用中
     */
    private Integer status;

    /**
     * 备注，想写啥写啥，补充说明用的
     */
    private String remark;

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
     * 1=已删除（其实还在数据库里，只是查不出来了）
     * 用的mybatis-plus的@TableLogic  // ← 逻辑删除标记：deleteById() 变成 UPDATE SET deleted=1
     */
    @TableLogic  // ← 逻辑删除标记：deleteById() 变成 UPDATE SET deleted=1
    private Integer deleted;

    // ==================== 非数据库字段（查询时额外封装的） ====================

    /**
     * 上级机构名称，数据库里没有这个字段
     * 是联表查出来的，方便前端直接显示上级名字，不用再查一遍
     */
    @TableField(exist = false)  // ← 表里没有此字段，Service 查询后手动填充（页面显示用）
    private String parentName;
}
