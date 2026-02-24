package com.zjgs.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjgs.backend.entity.Product;
import com.zjgs.backend.entity.Vo.ProductQueryVo;
import com.zjgs.backend.service.IProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 电瓶商品表 前端控制器
 * </p>
 *
 * @author Luke
 * @since 2026-02-24
 */
@Tag(name = "电瓶商品管理") // Swagger 分类名称
@RestController
@RequestMapping("/product")
@CrossOrigin // 允许前端跨域访问
public class ProductController {

    @Autowired
    private IProductService productService;

    // 接口功能：分页查询电瓶库存
    @PostMapping("/list")
    public Object getList(@RequestBody ProductQueryVo vo) {
        // 1. 准备分页对象
        Page<Product> pageParam = new Page<>(vo.getPage(), vo.getSize());

        // 2. 准备查询条件
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        // 如果前端传了品牌，就模糊查询
        if (StringUtils.hasText(vo.getBrand())) {
            wrapper.like(Product::getBrand, vo.getBrand());
        }
        // 如果前端传了型号，就模糊查询
        if (StringUtils.hasText(vo.getModel())) {
            wrapper.like(Product::getModel, vo.getModel());
        }
        // 按创建时间倒序排（新录入的在前面）
        wrapper.orderByDesc(Product::getCreateTime);

        // 3. 查询数据库
        Page<Product> result = productService.page(pageParam, wrapper);

        // 4. 返回结果
        return result;
    }
}
