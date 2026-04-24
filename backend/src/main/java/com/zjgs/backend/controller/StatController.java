package com.zjgs.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zjgs.backend.common.utils.RespBean;
import com.zjgs.backend.entity.Customer;
import com.zjgs.backend.entity.OrderItem;
import com.zjgs.backend.entity.Orders;
import com.zjgs.backend.entity.Product;
import com.zjgs.backend.entity.vo.StatVo;
import com.zjgs.backend.mapper.ProductMapper; // 🔴 引入
import com.zjgs.backend.service.ICustomerService;
import com.zjgs.backend.service.IOrderItemService;
import com.zjgs.backend.service.IOrdersService;
import com.zjgs.backend.service.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "统计分析")
@RestController
@RequestMapping("/stat")
@CrossOrigin
public class StatController {

    @Autowired
    private IOrdersService ordersService;
    @Autowired
    private IProductService productService;
    @Autowired
    private ICustomerService customerService;
    @Autowired
    private IOrderItemService orderItemService;

    @Autowired
    private ProductMapper productMapper; // 🔴 注入 Mapper 以使用手写 SQL

    @Operation(summary = "获取仪表盘综合统计数据")
    @GetMapping("/data")
    public RespBean getStatData() {
        StatVo vo = new StatVo();

        // 1. 今日销售额 (修正逻辑：使用 CURDATE() 函数，统计今天所有的订单金额)
        Map<String, Object> todayMap = ordersService.getMap(new QueryWrapper<Orders>()
                .select("sum(total_amount) as total")
                .apply("DATE(create_time) = CURDATE()"));
        vo.setTodaySales(todayMap != null && todayMap.get("total") != null ?
                Double.parseDouble(todayMap.get("total").toString()) : 0.0);

        // 2. 累计已收金额 (status = 'paid')
        Map<String, Object> totalSalesMap = ordersService.getMap(new QueryWrapper<Orders>()
                .select("sum(total_amount) as total").eq("status", "paid"));
        vo.setTotalSales(totalSalesMap != null && totalSalesMap.get("total") != null ?
                Double.parseDouble(totalSalesMap.get("total").toString()) : 0.0);

        // 3. 待收挂账总额 (status = 'debt')
        Map<String, Object> totalDebtMap = ordersService.getMap(new QueryWrapper<Orders>()
                .select("sum(total_amount) as total").eq("status", "debt"));
        vo.setTotalDebt(totalDebtMap != null && totalDebtMap.get("total") != null ?
                Double.parseDouble(totalDebtMap.get("total").toString()) : 0.0);

        // 4. 库存预警数量 (stock < 10)
        vo.setLowStockCount(productService.count(new QueryWrapper<Product>().le("stock", 10)));

        // 5. 品牌库存分布 (使用 Mapper 里的联表查询)
        vo.setBrandPieList(productMapper.getStockDistribution());

        // 6. 月度销售趋势 (年度视图)
        vo.setMonthlySalesTrend(ordersService.listMaps(new QueryWrapper<Orders>()
                .select("DATE_FORMAT(create_time, '%m月') as name", "sum(total_amount) as value")
                .groupBy("name").orderByAsc("name")));

        // 7. 每日销售走势 (最近30天视图)
        vo.setDailySalesTrend(ordersService.listMaps(new QueryWrapper<Orders>()
                .select("DATE_FORMAT(create_time, '%m-%d') as name", "sum(total_amount) as value")
                .apply("create_time >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)")
                .groupBy("name").orderByAsc("name")));

        // 8. 客户类型分布
        vo.setCustomerTypeDist(customerService.listMaps(new QueryWrapper<Customer>()
                .select("type as name", "count(*) as value").groupBy("type")));

        // 9. 品牌销量排行 (TOP 5)
        vo.setBrandSalesRanking(orderItemService.listMaps(new QueryWrapper<OrderItem>()
                .select("product_name as name", "sum(quantity) as value")
                .groupBy("name").orderByDesc("value").last("limit 5")));

        return RespBean.ok().data("info", vo);
    }
}