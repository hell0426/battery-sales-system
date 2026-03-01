package com.zjgs.backend.controller;

import com.zjgs.backend.common.utils.RespBean;
import com.zjgs.backend.entity.vo.OrderSubmitVo;
import com.zjgs.backend.service.IOrdersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "销售订单管理")
@RestController
@RequestMapping("/orders")
@CrossOrigin
public class OrdersController {

    @Autowired
    private IOrdersService ordersService;

    @Operation(summary = "提交订单(开单)")
    @PostMapping("/submit")
    public RespBean submitOrder(@RequestBody OrderSubmitVo vo) {
        try {
            ordersService.createOrder(vo);
            return RespBean.ok().msg("开单成功！");
        } catch (Exception e) {
            e.printStackTrace();
            // 捕获异常（比如库存不足）并告诉前端
            return RespBean.err().msg("开单失败：" + e.getMessage());
        }
    }
}