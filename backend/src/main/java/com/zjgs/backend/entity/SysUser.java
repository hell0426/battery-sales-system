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
import java.time.LocalDateTime;

/**
 * <p>
 * 系统用户表
 * </p>
 *
 * @author Luke
 * @since 2026-04-23
 */
@Data
@TableName("sys_user")
@Schema(name = "SysUser", description = "系统用户表")
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 账号
     */
    @TableField("username")
    @Schema(description = "账号")
    private String username;

    /**
     * 密码
     */
    @TableField("password")
    @Schema(description = "密码")
    private String password;

    /**
     * 真实姓名
     */
    @TableField("real_name")
    @Schema(description = "真实姓名")
    private String realName;

    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 角色
     */
    @TableField("role")
    @Schema(description = "角色")
    private String role;
}
