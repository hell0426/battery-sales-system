package com.zjgs.backend.service;

import com.zjgs.backend.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjgs.backend.entity.vo.OrderSubmitVo;

/**
 * <p>
 * 销售订单表 服务类
 * </p>
 *
 * @author Luke
 * @since 2026-02-24
 */
public interface IOrdersService extends IService<Orders> {
    // 定义开单方法
    void createOrder(OrderSubmitVo vo);
}
