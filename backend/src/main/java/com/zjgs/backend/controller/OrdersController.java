package com.zjgs.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjgs.backend.common.utils.RespBean;
import com.zjgs.backend.entity.OrderItem;
import com.zjgs.backend.entity.vo.OrderSubmitVo;
import com.zjgs.backend.entity.vo.SalesQueryVo;
import com.zjgs.backend.service.IOrderItemService;
import com.zjgs.backend.service.IOrdersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "销售订单管理")
@RestController
@RequestMapping("/orders")
@CrossOrigin
public class OrdersController {

    @Autowired
    private IOrdersService ordersService;

    @Autowired
    private IOrderItemService orderItemService;
    @Autowired
    private com.zjgs.backend.mapper.OrderItemMapper orderItemMapper;
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

    @Operation(summary = "销售明细统计查询")
    @PostMapping("/stats/list")
    public RespBean getSalesStats(@RequestBody SalesQueryVo vo) {
        // 1. 分页对象
        Page<OrderItem> pageParam = new Page<>(vo.getPage(), vo.getSize());

        // 2. 构造查询条件
        QueryWrapper<OrderItem> wrapper = new QueryWrapper<>();

        // 核心数据权限逻辑开始
        // 如果传过来的角色是 staff（员工），强制加上 user_id 过滤
        if ("staff".equals(vo.getRole())) {
            wrapper.eq("o.user_id", vo.getUserId());
        }
        // 如果是 admin，则不加这个 eq 条件，即查询全部
        // 核心数据权限逻辑结束

        // 按品牌筛选
        if (StringUtils.hasText(vo.getBrand())) {
            wrapper.like("product_name", vo.getBrand()); // 之前明细表存了"品牌-型号"快照
        }
        // 按型号筛选
        if (StringUtils.hasText(vo.getModel())) {
            wrapper.like("product_name", vo.getModel());
        }
        // 增加客户姓名过滤 (注意这里 c 是 customer 表的别名)
        if (StringUtils.hasText(vo.getCustomerName())) {
            wrapper.like("c.name", vo.getCustomerName());
        }
        // 销售员姓名筛选 ---
        // 注意：这里的 "u.real_name" 必须对应你在 Mapper SQL 里给 sys_user 表起的别名 'u'
        if (StringUtils.hasText(vo.getUserName())) {
            wrapper.like("u.real_name", vo.getUserName());
        }
        // 核心：按日期范围筛选（利用子查询关联 orders 表的时间）
        if (vo.getDateRange() != null && vo.getDateRange().size() == 2) {
            wrapper.inSql("order_id", "SELECT id FROM orders WHERE create_time BETWEEN '"
                    + vo.getDateRange().get(0) + " 00:00:00' AND '" + vo.getDateRange().get(1) + " 23:59:59'");
        }

        // 3. 关键修改：调用刚才 Mapper 里写的新统计方法
        Double totalAmount = orderItemMapper.selectSumAmount(wrapper);
        if (totalAmount == null) totalAmount = 0.0;
        // 4. 执行分页查询
        wrapper.orderByDesc("oi.id"); // 按ID倒序，让最新的在前面
        // 关键修改：调用 mapper 的新方法
        orderItemMapper.selectSalesStatsPage(pageParam, wrapper);

        return RespBean.ok()
                .data("list", pageParam.getRecords())
                .data("total", pageParam.getTotal())
                .data("summaryAmount", totalAmount);
    }
}