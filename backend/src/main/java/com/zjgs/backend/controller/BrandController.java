package com.zjgs.backend.controller;

import com.zjgs.backend.common.utils.RespBean;
import com.zjgs.backend.entity.Brand;
import com.zjgs.backend.service.IBrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "品牌管理")
@RestController
@RequestMapping("/brand") // 对应前端 api/category.js 里的 /brand
@CrossOrigin
public class BrandController {

    @Autowired
    private IBrandService brandService;

    // 重点：修改和新增都走这一个方法
    @Operation(summary = "保存或修改品牌")
    @PostMapping("/save")
    public RespBean save(@RequestBody Brand brand) {
        // MyBatis-Plus 的 saveOrUpdate 方法会自动判断：
        // 如果传入的对象有 id，就执行 UPDATE（修改）
        // 如果没有 id，就执行 INSERT（新增）
        boolean result = brandService.saveOrUpdate(brand);
        return result ? RespBean.ok().msg("保存成功") : RespBean.err().msg("保存失败");
    }

    @Operation(summary = "删除品牌")
    @DeleteMapping("/{id}")
    public RespBean delete(@PathVariable Integer id) {
        boolean result = brandService.removeById(id);
        return result ? RespBean.ok().msg("删除成功") : RespBean.err().msg("删除失败");
    }
}