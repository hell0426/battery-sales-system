package com.zjgs.backend.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjgs.backend.common.utils.RespBean;
import com.zjgs.backend.entity.Product;
import com.zjgs.backend.entity.vo.ProductQueryVo;
import com.zjgs.backend.service.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Tag(name = "电瓶商品管理")
@RestController
@RequestMapping("/product")
@CrossOrigin
public class ProductController {

    @Autowired
    private IProductService productService;

    // --- 1. 分页查询 (模仿你以前的 getCourses 写法) ---
    @Operation(summary = "分页条件查询")
    @PostMapping("/list") // 注意：因为 Vo 是 @RequestBody 传参，建议用 PostMapping
    public RespBean getList(@RequestBody ProductQueryVo vo) {
        // 1. 创建分页对象
        Page<Product> pageParam = new Page<>(vo.getPage(), vo.getSize());

        // 2. 创建查询条件 (推荐用 LambdaQueryWrapper，防止字段名写错)
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        // 品牌查询
        if (StringUtils.hasText(vo.getBrand())) {
            wrapper.like(Product::getBrand, vo.getBrand());
        }
        // 型号查询
        if (StringUtils.hasText(vo.getModel())) {
            wrapper.like(Product::getModel, vo.getModel());
        }
        // 按时间倒序
        wrapper.orderByDesc(Product::getCreateTime);

        // 3. 调用业务层
        productService.page(pageParam, wrapper);

        // 4. 返回结果 (完全按照你以前的习惯)
        return RespBean.ok()
                .data("list", pageParam.getRecords()) // 数据列表
                .data("total", pageParam.getTotal()); // 总条数
    }

    // --- 2. 新增 ---
    @Operation(summary = "新增电瓶")
    @PostMapping("/add")
    public RespBean add(@RequestBody Product product) {
        boolean result = productService.save(product);
        if (result) {
            return RespBean.ok().msg("新增成功");
        } else {
            return RespBean.err().msg("新增失败");
        }
    }

    // --- 3. 修改 ---
    @Operation(summary = "更新电瓶")
    @PostMapping("/update")
    public RespBean update(@RequestBody Product product) {
        boolean result = productService.updateById(product);
        if (result) {
            return RespBean.ok().msg("更新成功");
        } else {
            return RespBean.err().msg("更新失败");
        }
    }

    // --- 4. 删除 ---
    @Operation(summary = "删除电瓶")
    @DeleteMapping("/delete/{id}")
    public RespBean delete(@PathVariable Integer id) {
        boolean result = productService.removeById(id);
        if (result) {
            return RespBean.ok().msg("删除成功");
        } else {
            return RespBean.err().msg("删除失败");
        }
    }

    // --- 5. 回显详情 ---
    @Operation(summary = "根据ID获取详情")
    @GetMapping("/get/{id}")
    public RespBean getOne(@PathVariable Integer id) {
        Product product = productService.getById(id);
        return RespBean.ok().data("info", product);
    }
}