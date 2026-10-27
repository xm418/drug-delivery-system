package com.medical.drugdelivery.service;

import com.medical.drugdelivery.common.PageResult;
import com.medical.drugdelivery.dto.DeliveryQueryDTO;
import com.medical.drugdelivery.entity.DeliveryTask;

/**
 * 配送任务Service接口
 * 订单审核通过后生成配送任务，配送员这边领任务、发货、确认送达
 */
public interface DeliveryTaskService {

    /** 分页查询配送任务列表 */
    PageResult<DeliveryTask> page(DeliveryQueryDTO queryDTO);

    /** 根据id查任务详情 */
    DeliveryTask getById(Long id);

    /** 创建配送任务，订单审核通过后调用 */
    void create(Long orderId, Long fromInstitutionId, Long toInstitutionId, String deliveryPersonName, String deliveryPhone, String remark);

    /** 更新任务状态 */
    void updateStatus(Long id, Integer status, String remark);

    /** 开始配送，状态从待配送变成配送中 */
    void startDelivery(Long id);

    /** 确认送达，完成这次配送 */
    void completeDelivery(Long id);
}
