package com.zjgs.backend.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjgs.backend.common.utils.RespBean;
import com.zjgs.backend.entity.OrderItem;
import com.zjgs.backend.entity.Product;
import com.zjgs.backend.entity.vo.ProductQueryVo;
import com.zjgs.backend.service.IOrderItemService;
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
    @Autowired
    private IOrderItemService orderItemService; // 注入订单明细服务
    @Autowired
    private com.zjgs.backend.mapper.ProductMapper productMapper;

    // --- 1. 分页查询 (模仿你以前的 getCourses 写法) ---
    @Operation(summary = "分页条件查询")
    @PostMapping("/list")
    public RespBean getList(@RequestBody ProductQueryVo vo) {
        Page<Product> pageParam = new Page<>(vo.getPage(), vo.getSize());

        // 这里依然可以用 LambdaQueryWrapper，但查询时要用我们定义的 mapper 方法
        // 注意：这里的 wrapper 里的字段名要写 SQL 里的别名或者原始列名
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.eq("p.is_deleted", 0);
        if (StringUtils.hasText(vo.getBrand())) {
            wrapper.like("b.name", vo.getBrand()); // 注意：这里连的是品牌表的别名 b
        }
        if (StringUtils.hasText(vo.getModel())) {
            wrapper.like("m.name", vo.getModel()); // 注意：这里连的是型号表的别名 m
        }
        wrapper.orderByDesc("p.create_time");

        // 调用自定义的联表方法
        productMapper.selectProductPage(pageParam, wrapper);

        return RespBean.ok()
                .data("list", pageParam.getRecords())
                .data("total", pageParam.getTotal());
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
    @DeleteMapping("/delete/{id}")
    public RespBean delete(@PathVariable Integer id) {
        // 检查订单明细表里是否有这个电瓶的销售记录
        long saleCount = orderItemService.count(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getProductId, id));

        if (saleCount > 0) {
            return RespBean.err().msg("该电瓶已有销售记录，不可删除，建议修改库存为0或进行下架处理！");
        }

        if(productService.removeById(id)) {
            return RespBean.ok().msg("删除成功");
        }
        return RespBean.err().msg("删除失败");
    }

    // --- 5. 回显详情 ---
    @Operation(summary = "根据ID获取详情")
    @GetMapping("/get/{id}")
    public RespBean getOne(@PathVariable Integer id) {
        Product product = productService.getById(id);
        return RespBean.ok().data("info", product);
    }
}