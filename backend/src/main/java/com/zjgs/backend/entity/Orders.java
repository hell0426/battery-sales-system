package com.zjgs.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
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
 * 销售订单表
 * </p>
 *
 * @author Luke
 * @since 2026-02-24
 */
@Getter
@Setter
@ToString
@TableName("orders")
@Schema(name = "Orders", description = "销售订单表")
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 客户ID
     */
    @TableField("customer_id")
    @Schema(description = "客户ID")
    private Integer customerId;

    /**
     * 订单总金额
     */
    @TableField("total_amount")
    @Schema(description = "订单总金额")
    private BigDecimal totalAmount;

    /**
     * 状态: paid(已付), debt(挂账)
     */
    @TableField("status")
    @Schema(description = "状态: paid(已付), debt(挂账)")
    private String status;

    /**
     * 销售时间
     */
    @TableField("create_time")
    @Schema(description = "销售时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除(1:已删, 0:未删)
     */
    @TableLogic
    @TableField("is_deleted")
    @Schema(description = "逻辑删除(1:已删, 0:未删)")
    private Integer isDeleted;
}
