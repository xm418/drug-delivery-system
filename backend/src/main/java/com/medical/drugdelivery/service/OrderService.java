package com.medical.drugdelivery.service;

import com.medical.drugdelivery.common.PageResult;
import com.medical.drugdelivery.dto.OrderCreateDTO;
import com.medical.drugdelivery.dto.OrderQueryDTO;
import com.medical.drugdelivery.entity.DrugOrder;

/**
 * 订单Service接口
 * 药品订购的核心流程：下单 -> 审核 -> 配送 -> 完成
 */
public interface OrderService {

    /** 分页查询订单列表 */
    PageResult<DrugOrder> page(OrderQueryDTO queryDTO);

    /** 根据id查订单 */
    DrugOrder getById(Long id);

    /** 查订单详情，连明细一起查出来 */
    DrugOrder getDetail(Long id);

    /** 创建订单，生成订单号并保存订单明细 */
    void create(OrderCreateDTO createDTO, Long userId);

    /** 审核订单，approved=1通过 2驳回，通过后扣库存并生成配送任务 */
    void approve(Long id, Integer approved, String remark, Long operatorId);

    /** 取消订单，只有待审核的订单能取消 */
    void cancel(Long id, Long userId);
}
