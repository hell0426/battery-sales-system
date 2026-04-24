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
 *
 * @author Luke
 * @since 2026-02-24
 */
public interface OrderItemMapper extends BaseMapper<OrderItem> {
    // 核心 SQL：四表联查 (明细+订单+客户+型号)
    // 逻辑：从明细表出发，关联订单表拿到客户ID，再关联客户表拿到名字
    @Select("SELECT oi.*, c.name as customerName " +
            "FROM order_item oi " +
            "JOIN orders o ON oi.order_id = o.id " +
            "JOIN customer c ON o.customer_id = c.id " +
            "${ew.customSqlSegment}")
    Page<OrderItem> selectSalesStatsPage(Page<OrderItem> page, @Param(Constants.WRAPPER) Wrapper query);

    // 2. 新增：带 JOIN 的总金额统计方法
    @Select("SELECT SUM(oi.price * oi.quantity) " +
            "FROM order_item oi " +
            "JOIN orders o ON oi.order_id = o.id " +
            "JOIN customer c ON o.customer_id = c.id " +
            "${ew.customSqlSegment}")
    Double selectSumAmount(@Param(Constants.WRAPPER) Wrapper query);
}
