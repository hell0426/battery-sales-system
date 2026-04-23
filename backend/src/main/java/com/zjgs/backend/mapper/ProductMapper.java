package com.zjgs.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjgs.backend.entity.Product;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 电瓶商品表 Mapper 接口
 * </p>
 *
 * @author Luke
 * @since 2026-02-24
 */
public interface ProductMapper extends BaseMapper<Product> {
    // 核心 SQL：三表联查
    @Select("SELECT p.*, b.name as brandName, m.name as modelName " +
            "FROM product p " +
            "LEFT JOIN product_model m ON p.model_id = m.id " +
            "LEFT JOIN brand b ON m.brand_id = b.id " +
            "${ew.customSqlSegment}")
    Page<Product> selectProductPage(Page<Product> page, @Param("ew") com.baomidou.mybatisplus.core.conditions.Wrapper query);
}
