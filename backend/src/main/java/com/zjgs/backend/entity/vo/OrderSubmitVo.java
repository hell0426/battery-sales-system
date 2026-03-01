package com.zjgs.backend.entity.vo;

import lombok.Data;
import java.util.List;

@Data
public class OrderSubmitVo {
    // 客户ID
    private Integer customerId;
    // 支付状态 (paid:现结, debt:挂账)
    private String status;
    // 买了哪些东西？(是一个列表)
    private List<OrderItemVo> items;

    // 内部类：定义购物车里的每一项
    @Data
    public static class OrderItemVo {
        private Integer productId;
        private Integer quantity; // 买的数量
        private Double price;     // 当时卖的价格
    }
}