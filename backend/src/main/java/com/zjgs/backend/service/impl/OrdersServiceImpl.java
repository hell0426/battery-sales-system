package com.zjgs.backend.service.impl;

import com.zjgs.backend.entity.Orders;
import com.zjgs.backend.mapper.OrdersMapper;
import com.zjgs.backend.service.IOrdersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 销售订单表 服务实现类
 * </p>
 *
 * @author Luke
 * @since 2026-02-24
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements IOrdersService {

}
