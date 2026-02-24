package com.zjgs.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 订单明细表
 * </p>
 *
 * @author Luke
 * @since 2026-02-24
 */
@Getter
@Setter
@ToString
@TableName("order_item")
@Schema(name = "OrderItem", description = "订单明细表")
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 归属订单ID
     */
    @TableField("order_id")
    @Schema(description = "归属订单ID")
    private Integer orderId;

    /**
     * 商品ID
     */
    @TableField("product_id")
    @Schema(description = "商品ID")
    private Integer productId;

    /**
     * 商品名快照
     */
    @TableField("product_name")
    @Schema(description = "商品名快照")
    private String productName;

    /**
     * 购买数量
     */
    @TableField("quantity")
    @Schema(description = "购买数量")
    private Integer quantity;

    /**
     * 成交单价
     */
    @TableField("price")
    @Schema(description = "成交单价")
    private BigDecimal price;

    /**
     * 创建时间
     */
    @TableField("create_time")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
