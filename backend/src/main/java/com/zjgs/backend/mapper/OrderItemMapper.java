package com.zjgs.backend.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjgs.backend.entity.OrderItem;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 订单明细表 Mapper 接口
 * </p>
 */
public interface OrderItemMapper extends BaseMapper<OrderItem> {

    // 1. 分页统计方法
    @Select("SELECT oi.*, c.name as customerName, u.real_name as userName, o.discount_amount as discountAmount " + // 重点：结尾加了空格
            "FROM order_item oi " +
            "JOIN orders o ON oi.order_id = o.id " +
            "JOIN customer c ON o.customer_id = c.id " +
            "LEFT JOIN sys_user u ON o.user_id = u.id " +
            "${ew.customSqlSegment}")
    Page<OrderItem> selectSalesStatsPage(Page<OrderItem> page, @Param(Constants.WRAPPER) Wrapper query);

    // 2. 总金额统计方法 (注意：这里直接查订单表的实收金额更准确)
    @Select("SELECT SUM(o.total_amount) " +
            "FROM order_item oi " +
            "JOIN orders o ON oi.order_id = o.id " +
            "JOIN customer c ON o.customer_id = c.id " +
            "LEFT JOIN sys_user u ON o.user_id = u.id " +
            "${ew.customSqlSegment}")
    Double selectSumAmount(@Param(Constants.WRAPPER) Wrapper query);
}