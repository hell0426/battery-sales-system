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

    @ExcelProperty("商品名称")
    @ColumnWidth(30)
    private String productName;

    @ExcelProperty("成交单价")
    @ColumnWidth(15)
    private BigDecimal price;

    @ExcelProperty("数量")
    @ColumnWidth(10)
    private Integer quantity;

    @ExcelProperty("成交时间")
    @ColumnWidth(25)
    private String createTime;
}