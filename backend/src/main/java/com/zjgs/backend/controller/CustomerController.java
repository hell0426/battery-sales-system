package com.zjgs.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjgs.backend.common.utils.RespBean;
import com.zjgs.backend.entity.Customer;
import com.zjgs.backend.entity.vo.CustomerQueryVo;
import com.zjgs.backend.service.ICustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Tag(name = "客户管理")
@RestController
@RequestMapping("/customer")
@CrossOrigin
public class CustomerController {

    @Autowired
    private ICustomerService customerService;

    // 1. 分页查询
    @Operation(summary = "分页查询客户")
    @PostMapping("/list")
    public RespBean getList(@RequestBody CustomerQueryVo vo) {
        Page<Customer> pageParam = new Page<>(vo.getPage(), vo.getSize());
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(vo.getName())) {
            wrapper.like(Customer::getName, vo.getName());
        }
        if (StringUtils.hasText(vo.getPhone())) {
            wrapper.like(Customer::getPhone, vo.getPhone());
        }
        wrapper.orderByDesc(Customer::getCreateTime);

        customerService.page(pageParam, wrapper);

        return RespBean.ok()
                .data("list", pageParam.getRecords())
                .data("total", pageParam.getTotal());
    }

    // 2. 新增
    @Operation(summary = "新增客户")
    @PostMapping("/add")
    public RespBean add(@RequestBody Customer customer) {
        if(customerService.save(customer)) {
            return RespBean.ok().msg("新增成功");
        }
        return RespBean.err().msg("新增失败");
    }

    // 3. 修改
    @Operation(summary = "修改客户")
    @PostMapping("/update")
    public RespBean update(@RequestBody Customer customer) {
        if(customerService.updateById(customer)) {
            return RespBean.ok().msg("修改成功");
        }
        return RespBean.err().msg("修改失败");
    }

    // 4. 删除
    @Operation(summary = "删除客户")
    @DeleteMapping("/delete/{id}")
    public RespBean delete(@PathVariable Integer id) {
        if(customerService.removeById(id)) {
            return RespBean.ok().msg("删除成功");
        }
        return RespBean.err().msg("删除失败");
    }
}