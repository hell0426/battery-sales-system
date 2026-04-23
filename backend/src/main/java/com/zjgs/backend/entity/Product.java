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
 * 电瓶商品表
 * </p>
 *
 * @author Luke
 * @since 2026-02-24
 */
@Getter
@Setter
@ToString
@TableName("product")
@Schema(name = "Product", description = "电瓶商品表")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    // 重点：虚拟字段，加 exist = false 注解
    @TableField(exist = false)
    @Schema(description = "品牌名")
    private String brandName;

    @TableField(exist = false)
    @Schema(description = "型号名")
    private String modelName;

    /**
     * 进价
     */
    @TableField("cost_price")
    @Schema(description = "进价")
    private BigDecimal costPrice;

    /**
     * 售价
     */
    @Schema(description = "售价")
    @TableField("selling_price")
    private BigDecimal sellingPrice;

    /**
     * 库存数量
     */
    @TableField("stock")
    @Schema(description = "库存数量")
    private Integer stock;

    /**
     * 创建时间
     */
    @TableField("create_time")
    @Schema(description = "创建时间")
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
    /**
     * 关联型号表id
     */
    @TableField("model_id")
    @Schema(description = "关联型号表id")
    private Integer modelId;
}
