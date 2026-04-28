package com.zjgs.backend.entity.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class SalesExportVo {
    @ExcelProperty("序号")
    @ColumnWidth(10)
    private Integer id;

    @ExcelProperty("购买客户")
    @ColumnWidth(20)
    private String customerName;

    // Excel 显示销售员
    @ExcelProperty("销售员")
    @ColumnWidth(15)
    private String userName;

    @ExcelProperty("商品名称")
    @ColumnWidth(30)
    private String productName;

    @ExcelProperty("成交单价")
    @ColumnWidth(15)
    private BigDecimal price;

    @ExcelProperty("数量")
    @ColumnWidth(10)
    private Integer quantity;

    // 1. 新增：原价小计 (不含折扣)
    @ExcelProperty("原价小计")
    private BigDecimal originalTotal;

    // 2. 新增：订单优惠金额
    @ExcelProperty("订单优惠")
    private BigDecimal discountAmount;

    // 3. 新增：最终实收金额
    @ExcelProperty("实收金额")
    private BigDecimal finalAmount;

    @ExcelProperty("成交时间")
    @ColumnWidth(25)
    private String createTime;
}