package com.zjgs.backend.entity.vo;

import lombok.Data;
import java.util.Map;
import java.util.List;

@Data
public class StatVo {
    private Double totalSales; // 总销售额 (已结)
    private Double totalDebt;  // 总欠款 (挂账)
    private Long lowStockCount; // 库存预警数量

    // 图表数据
    private List<Map<String, Object>> brandPieList; // 品牌占比数据
}