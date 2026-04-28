package com.zjgs.backend.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjgs.backend.common.utils.RespBean;
import com.zjgs.backend.entity.OrderItem;
import com.zjgs.backend.entity.vo.SalesExportVo;
import com.zjgs.backend.entity.vo.SalesQueryVo;
import com.zjgs.backend.service.IOrderItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * <p>
 * 订单明细表 前端控制器
 * </p>
 *
 * @author Luke
 * @since 2026-02-24
 */
@Tag(name = "订单明细")
@RestController
@RequestMapping("/orderItem")
@CrossOrigin
public class OrderItemController {

    @Autowired
    private IOrderItemService orderItemService;
    @Autowired
    private com.zjgs.backend.mapper.OrderItemMapper orderItemMapper;
    @Operation(summary = "根据订单ID查询明细")
    @GetMapping("/list/{orderId}")
    public RespBean getItemsByOrderId(@PathVariable Integer orderId) {
        List<OrderItem> list = orderItemService.lambdaQuery()
                .eq(OrderItem::getOrderId, orderId)
                .list();
        return RespBean.ok().data("items", list);
    }

    @Operation(summary = "导出销售统计Excel")
    @PostMapping("/stats/export")
    public void exportSales(@RequestBody SalesQueryVo vo, HttpServletResponse response) throws Exception {
        // 1. 设置响应格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("销售统计报表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        // 2. 获取符合条件的所有数据（不分页，查全部）
        QueryWrapper<OrderItem> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(vo.getBrand())) wrapper.like("oi.product_name", vo.getBrand());
        if (StringUtils.hasText(vo.getModel())) wrapper.like("oi.product_name", vo.getModel());
        if (StringUtils.hasText(vo.getCustomerName())) wrapper.like("c.name", vo.getCustomerName());
        // 加上销售员的筛选
        if (StringUtils.hasText(vo.getUserName())) {
            wrapper.like("u.real_name", vo.getUserName());
        }
        if (vo.getDateRange() != null && vo.getDateRange().size() == 2) {
            wrapper.between("o.create_time", vo.getDateRange().get(0) + " 00:00:00", vo.getDateRange().get(1) + " 23:59:59");
        }

        // 调用你之前写好的那个带 JOIN 的 mapper 方法，传一个很大的分页数来获取全部记录
        List<OrderItem> list = orderItemMapper.selectSalesStatsPage(new Page<>(1, 100000), wrapper).getRecords();

        // 3. 转换成导出模型
        List<SalesExportVo> exportList = list.stream().map(item -> {
            SalesExportVo exportVo = new SalesExportVo();
            exportVo.setId(item.getId());
            exportVo.setCustomerName(item.getCustomerName());
            // 把查出来的员工名字塞进导出模型
            exportVo.setUserName(item.getUserName());
            exportVo.setProductName(item.getProductName());
            exportVo.setPrice(item.getPrice());
            exportVo.setQuantity(item.getQuantity());
            exportVo.setCreateTime(item.getCreateTime().toString().replace("T", " "));
            return exportVo;
        }).toList();

        // 4. 写出到响应流
        EasyExcel.write(response.getOutputStream(), SalesExportVo.class)
                .sheet("销售统计数据")
                .doWrite(exportList);
    }
}
