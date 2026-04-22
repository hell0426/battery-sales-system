package com.zjgs.backend.controller;

import com.zjgs.backend.common.utils.RespBean;
import com.zjgs.backend.entity.Brand;
import com.zjgs.backend.entity.ProductModel;
import com.zjgs.backend.service.IBrandService;
import com.zjgs.backend.service.IProductModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "基础分类管理")
@RestController
@RequestMapping("/category")
@CrossOrigin
public class CategoryController {

    @Autowired
    private IBrandService brandService;

    @Autowired
    private IProductModelService productModelService;

    @Operation(summary = "获取所有品牌列表")
    @GetMapping("/brands")
    public RespBean getAllBrands() {
        List<Brand> list = brandService.list();
        return RespBean.ok().data("items", list);
    }

    @Operation(summary = "根据品牌ID获取对应的型号")
    @GetMapping("/models/{brandId}")
    public RespBean getModelsByBrand(@PathVariable Integer brandId) {
        // 查询该品牌下的所有型号
        List<ProductModel> list = productModelService.lambdaQuery()
                .eq(ProductModel::getBrandId, brandId)
                .list();
        return RespBean.ok().data("items", list);
    }
}