package com.zjgs.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zjgs.backend.common.utils.RespBean;
import com.zjgs.backend.entity.Orders;
import com.zjgs.backend.entity.Product;
import com.zjgs.backend.entity.vo.StatVo;
import com.zjgs.backend.service.IOrdersService;
import com.zjgs.backend.service.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @Operation(summary = "获取首页统计数据")
    @GetMapping("/data")
    public RespBean getStatData() {
        StatVo vo = new StatVo();

        // 1. 统计总销售额 (状态为 paid)
        List<Orders> paidOrders = ordersService.list(new QueryWrapper<Orders>().eq("status", "paid"));
        double sales = paidOrders.stream().mapToDouble(o -> o.getTotalAmount().doubleValue()).sum();
        vo.setTotalSales(sales);

        // 2. 统计总欠款 (状态为 debt)
        List<Orders> debtOrders = ordersService.list(new QueryWrapper<Orders>().eq("status", "debt"));
        double debt = debtOrders.stream().mapToDouble(o -> o.getTotalAmount().doubleValue()).sum();
        vo.setTotalDebt(debt);

        // 3. 统计库存预警 (假设少于10个算缺货)
        long lowStock = productService.count(new QueryWrapper<Product>().le("stock", 10));
        vo.setLowStockCount(lowStock);

        // 4. 统计品牌占比 (SQL: select brand as name, count(*) as value from product group by brand)
        // MyBatis-Plus 的 selectMaps 方法可以直接返回 Map 列表
        QueryWrapper<Product> pieWrapper = new QueryWrapper<>();
        pieWrapper.select("brand as name", "count(*) as value").groupBy("brand");
        List<Map<String, Object>> pieList = productService.listMaps(pieWrapper);
        vo.setBrandPieList(pieList);

        return RespBean.ok().data("info", vo);
    }
}