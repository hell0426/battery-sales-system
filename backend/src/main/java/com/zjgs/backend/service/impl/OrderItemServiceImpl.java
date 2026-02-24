package com.zjgs.backend.service.impl;

import com.zjgs.backend.entity.OrderItem;
import com.zjgs.backend.mapper.OrderItemMapper;
import com.zjgs.backend.service.IOrderItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单明细表 服务实现类
 * </p>
 *
 * @author Luke
 * @since 2026-02-24
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements IOrderItemService {

}
