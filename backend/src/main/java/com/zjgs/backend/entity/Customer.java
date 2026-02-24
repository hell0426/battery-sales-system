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
import java.time.LocalDateTime;

/**
 * <p>
 * 客户信息表
 * </p>
 *
 * @author Luke
 * @since 2026-02-24
 */
@Getter
@Setter
@ToString
@TableName("customer")
@Schema(name = "Customer", description = "客户信息表")
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 客户名/修理厂名
     */
    @TableField("name")
    @Schema(description = "客户名/修理厂名")
    private String name;

    /**
     * 联系电话
     */
    @TableField("phone")
    @Schema(description = "联系电话")
    private String phone;

    /**
     * 类型: individual(散客), shop(修理厂)
     */
    @TableField("type")
    @Schema(description = "类型: individual(散客), shop(修理厂)")
    private String type;

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
}
