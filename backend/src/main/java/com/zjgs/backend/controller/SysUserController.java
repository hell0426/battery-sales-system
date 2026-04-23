package com.zjgs.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zjgs.backend.common.utils.RespBean;
import com.zjgs.backend.entity.SysUser;
import com.zjgs.backend.service.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 系统用户表 前端控制器
 * </p>
 *
 * @author Luke
 * @since 2026-04-23
 */
@Tag(name = "用户与员工管理")
@RestController
@RequestMapping("/sysUser") // 路径是 /sysUser
@CrossOrigin
public class SysUserController {

    @Autowired
    private ISysUserService sysUserService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public RespBean login(@RequestBody SysUser loginUser) {
        // 根据用户名查询数据库
        SysUser user = sysUserService.getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, loginUser.getUsername()));

        if (user == null) {
            return RespBean.err().msg("账号不存在");
        }

        // 校验密码
        if (!user.getPassword().equals(loginUser.getPassword())) {
            return RespBean.err().msg("密码错误");
        }

        // 登录成功，把完整的用户信息（包含role）回传给前端
        return RespBean.ok().msg("欢迎回来，" + user.getRealName()).data("userInfo", user);
    }

    @Operation(summary = "获取所有员工列表(登录页下拉框用)")
    @GetMapping("/list-all")
    public RespBean listAll() {
        List<SysUser> list = sysUserService.list();
        return RespBean.ok().data("items", list);
    }

    @Operation(summary = "新增或修改用户")
    @PostMapping("/save")
    public RespBean save(@RequestBody SysUser sysUser) {
        boolean result = sysUserService.saveOrUpdate(sysUser);
        return result ? RespBean.ok().msg("保存成功") : RespBean.err().msg("保存失败");
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public RespBean delete(@PathVariable Integer id) {
        boolean result = sysUserService.removeById(id);
        return result ? RespBean.ok().msg("删除成功") : RespBean.err().msg("删除失败");
    }
}