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
    // --- 追加在 OrdersController 内部 ---

    @Autowired
    private com.zjgs.backend.service.ICustomerService customerService; // 注入客户服务，为了显示名字

    // 1. 分页查询订单
    @Operation(summary = "查询订单列表")
    @PostMapping("/list")
    public RespBean getList(@RequestBody com.zjgs.backend.entity.vo.OrdersQueryVo vo) {
        // 1. 分页
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.zjgs.backend.entity.Orders> pageParam = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(vo.getPage(), vo.getSize());

        // 2. 条件
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.zjgs.backend.entity.Orders> wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        if (org.springframework.util.StringUtils.hasText(vo.getStatus())) {
            wrapper.eq(com.zjgs.backend.entity.Orders::getStatus, vo.getStatus());
        }
        wrapper.orderByDesc(com.zjgs.backend.entity.Orders::getCreateTime);

        // 3. 查询
        ordersService.page(pageParam, wrapper);

        // 4. (可选优化) 把 customerId 变成 名字，这里为了简单先只返回 ID，前端去匹配
        return RespBean.ok()
                .data("list", pageParam.getRecords())
                .data("total", pageParam.getTotal());
    }

    // 2. 结账 (把欠款变成已结)
    @Operation(summary = "结账/核销")
    @PostMapping("/settle/{id}")
    public RespBean settle(@PathVariable Integer id) {
        com.zjgs.backend.entity.Orders order = ordersService.getById(id);
        if (order == null) return RespBean.err().msg("订单不存在");

        order.setStatus("paid"); // 改为已支付
        ordersService.updateById(order);

        return RespBean.ok().msg("结账成功！");
    }
}