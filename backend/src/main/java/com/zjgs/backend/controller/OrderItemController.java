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

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

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
        // 数据权限：如果是员工，强制只导出他自己的订单
        if ("staff".equals(vo.getRole())) {
            wrapper.eq("o.user_id", vo.getUserId());
        }
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
            // 核心计算逻辑
            // 1. 原价小计 = 单价 * 数量
            BigDecimal original = item.getPrice().multiply(new BigDecimal(item.getQuantity()));
            exportVo.setOriginalTotal(original);

            // 2. 优惠金额 (直接从 item 拿，item 是 Mapper 查出来的)
            BigDecimal discount = item.getDiscountAmount() != null ? item.getDiscountAmount() : BigDecimal.ZERO;
            exportVo.setDiscountAmount(discount);

            // 3. 实收金额 = 原价小计 - 优惠
            exportVo.setFinalAmount(original.subtract(discount));

            exportVo.setCreateTime(item.getCreateTime().toString().replace("T", " "));
            return exportVo;
        }).collect(Collectors.toList());

        // 手动计算并追加“合计行” ---
        if (exportList != null && !exportList.isEmpty()) {
            int totalQuantity = 0;
            BigDecimal totalOriginal = BigDecimal.ZERO;
            BigDecimal totalDiscount = BigDecimal.ZERO;
            BigDecimal totalFinal = BigDecimal.ZERO;

            // 1. 遍历当前列表进行累加
            for (SalesExportVo item : exportList) {
                totalQuantity += (item.getQuantity() != null ? item.getQuantity() : 0);
                totalOriginal = totalOriginal.add(item.getOriginalTotal() != null ? item.getOriginalTotal() : BigDecimal.ZERO);
                totalDiscount = totalDiscount.add(item.getDiscountAmount() != null ? item.getDiscountAmount() : BigDecimal.ZERO);
                totalFinal = totalFinal.add(item.getFinalAmount() != null ? item.getFinalAmount() : BigDecimal.ZERO);
            }

            // 2. 创建一个专门的“合计”行对象
            SalesExportVo totalRow = new SalesExportVo();
            totalRow.setCustomerName("【全部合计】"); // 写在客户列，比较醒目
            totalRow.setUserName("-");
            totalRow.setProductName("-");
            totalRow.setPrice(null); // 单价不需要合计
            totalRow.setQuantity(totalQuantity);
            totalRow.setOriginalTotal(totalOriginal);
            totalRow.setDiscountAmount(totalDiscount);
            totalRow.setFinalAmount(totalFinal);
            totalRow.setCreateTime("-");

            // 3. 将合计行追加到列表末尾
            exportList.add(totalRow);
        }
        // --- 合计行逻辑结束 ---
        // 4. 写出到响应流
        EasyExcel.write(response.getOutputStream(), SalesExportVo.class)
                .sheet("销售统计数据")
                .doWrite(exportList);
    }
}
