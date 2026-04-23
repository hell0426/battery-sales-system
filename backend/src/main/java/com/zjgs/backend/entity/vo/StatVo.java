package com.zjgs.backend.entity.vo;

import lombok.Data;
import java.util.Map;
import java.util.List;

@Data
public class StatVo {
    // 卡片数据
    private Double totalSales;    // 累计已收
    private Double totalDebt;     // 待收挂账
    private Double todaySales;    // 今日销售额
    private Long lowStockCount;   // 预警型号数

    // 图表数据
    private List<Map<String, Object>> brandPieList;      // 品牌库存占比
    private List<Map<String, Object>> monthlySalesTrend; // 月度销售趋势 (年视图)
    private List<Map<String, Object>> dailySalesTrend;   // 每日销售趋势 (月视图)
    private List<Map<String, Object>> customerTypeDist;  // 客户类型分布
    private List<Map<String, Object>> brandSalesRanking; // 品牌销量排行
}