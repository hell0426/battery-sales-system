package com.zjgs.backend.controller;

import com.zjgs.backend.common.utils.RespBean;
import com.zjgs.backend.entity.ProductModel;
import com.zjgs.backend.service.IProductModelService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Luke
 * @since 2026-04-22
 */
@Tag(name = "型号管理")
@RestController
@RequestMapping("/productModel")
@CrossOrigin
public class ProductModelController {
    @Autowired
    private IProductModelService modelService;

    @GetMapping("/list")
    public RespBean list() {
        return RespBean.ok().data("items", modelService.list());
    }

    @PostMapping("/save")
    public RespBean save(@RequestBody ProductModel model) {
        return modelService.saveOrUpdate(model) ? RespBean.ok() : RespBean.err();
    }

    @DeleteMapping("/{id}")
    public RespBean delete(@PathVariable Integer id) {
        return modelService.removeById(id) ? RespBean.ok() : RespBean.err();
    }
}
