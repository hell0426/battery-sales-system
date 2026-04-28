package com.zjgs.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author Luke
 * @since 2026-04-22
 */
@Data
@TableName("product_model")
@Schema(name = "ProductModel", description = "")
public class ProductModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 所属品牌ID
     */
    @TableField("brand_id")
    @Schema(description = "所属品牌ID")
    private Integer brandId;

    /**
     * 型号名称
     */
    @TableField("name")
    @Schema(description = "型号名称")
    private String name;
}
