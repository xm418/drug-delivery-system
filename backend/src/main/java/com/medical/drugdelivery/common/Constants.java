package com.medical.drugdelivery.common;

/**
 * 系统常量类
 * 各种状态值、阈值都集中放这里，避免代码里到处写魔法数字
 * 数据库里存的也是这些数字，两边要保持一致
 */
public class Constants {

    // 不让new，纯静态使用
    private Constants() {}

    // 用户状态
    public static final Integer USER_STATUS_NORMAL = 1;
    public static final Integer USER_STATUS_DISABLED = 0;

    // 角色状态
    public static final Integer ROLE_STATUS_NORMAL = 1;
    public static final Integer ROLE_STATUS_DISABLED = 0;

    // 机构级别
    public static final Integer INSTITUTION_LEVEL_COUNTY = 1;  // 县级
    public static final Integer INSTITUTION_LEVEL_TOWNSHIP = 2; // 乡级
    public static final Integer INSTITUTION_LEVEL_VILLAGE = 3;  // 村级

    // 机构状态
    public static final Integer INSTITUTION_STATUS_NORMAL = 1;
    public static final Integer INSTITUTION_STATUS_DISABLED = 0;

    // 药品状态
    public static final Integer DRUG_STATUS_ON_SALE = 1;  // 在售
    public static final Integer DRUG_STATUS_OFF_SALE = 0; // 下架

    // 库存操作类型
    public static final Integer STOCK_OP_IN = 1;     // 入库
    public static final Integer STOCK_OP_OUT = 2;    // 出库
    public static final Integer STOCK_OP_TRANSFER = 3; // 调拨

    // 订单状态
    public static final Integer ORDER_STATUS_PENDING = 0;    // 待审核
    public static final Integer ORDER_STATUS_APPROVED = 1;   // 已审核
    public static final Integer ORDER_STATUS_REJECTED = 2;   // 已驳回
    public static final Integer ORDER_STATUS_DELIVERING = 3; // 配送中
    public static final Integer ORDER_STATUS_COMPLETED = 4;  // 已完成
    public static final Integer ORDER_STATUS_CANCELLED = 5;  // 已取消

    // 配送任务状态
    public static final Integer DELIVERY_STATUS_PENDING = 0;    // 待配送
    public static final Integer DELIVERY_STATUS_DELIVERING = 1; // 配送中
    public static final Integer DELIVERY_STATUS_DELIVERED = 2;  // 已送达
    public static final Integer DELIVERY_STATUS_COMPLETED = 3;  // 已完成

    // 默认密码
    public static final String DEFAULT_PASSWORD = "123456";

    // 库存预警阈值
    public static final Integer STOCK_WARNING_THRESHOLD = 10;
}
