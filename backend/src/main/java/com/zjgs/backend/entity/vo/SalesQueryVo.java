package com.zjgs.backend.entity.vo;

import lombok.Data;
import java.util.List;

@Data
public class SalesQueryVo {
    private Integer page = 1;
    private Integer size = 10;
    
    private String brand;      // 品牌
    private String model;      // 型号
    private String customerName; // 客户名称
    private List<String> dateRange; // 前端传来的 [开始日期, 结束日期]
}