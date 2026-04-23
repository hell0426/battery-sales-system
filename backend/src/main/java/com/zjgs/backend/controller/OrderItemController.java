package com.zjgs.backend.controller;

import com.zjgs.backend.common.utils.RespBean;
import com.zjgs.backend.entity.OrderItem;
import com.zjgs.backend.service.IOrderItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 订单明细表 前端控制器
 * </p>
 *
 * @author Luke
 * @since 2026-02-24
 */
@Tag(name = "订单明细")
@RestController
@RequestMapping("/orderItem")
@CrossOrigin
public class OrderItemController {

    @Autowired
    private IOrderItemService orderItemService;

    @Operation(summary = "根据订单ID查询明细")
    @GetMapping("/list/{orderId}")
    public RespBean getItemsByOrderId(@PathVariable Integer orderId) {
        List<OrderItem> list = orderItemService.lambdaQuery()
                .eq(OrderItem::getOrderId, orderId)
                .list();
        return RespBean.ok().data("items", list);
    }
}
