package com.example.backend.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.common.constants.LoginConstant;
import com.example.backend.common.model.PageResult;
import com.example.backend.common.result.BaseResponse;
import com.example.backend.common.result.Result;
import com.example.backend.entity.Permission;
import com.example.backend.service.IPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permission")
public class PermissionController {
    @Resource
    private IPermissionService permissionService;

    @Operation(summary = "获取当前用户权限码列表")
    @GetMapping("/current")
    public BaseResponse<List<String>> current() {
        Integer userId = (Integer) StpUtil.getSession().get(LoginConstant.USER_ID);
        return Result.success(permissionService.getPermissionCodesByUserId(userId));
    }

    @Operation(summary = "查询所有权限")
    @GetMapping("/list")
    public BaseResponse<List<Permission>> list() {
        return Result.success(permissionService.list());
    }

    @Operation(summary = "分页查询权限")
    @GetMapping("/page")
    public BaseResponse<PageResult<List<Permission>>> page(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) String permissionName) {
        QueryWrapper<Permission> qw = new QueryWrapper<>();
        if (permissionName != null && !permissionName.isEmpty()) {
            qw.like("permission_name", permissionName);
        }
        qw.orderByDesc("id");
        Page<Permission> page = permissionService.page(new Page<>(pageNum, pageSize), qw);
        PageResult<List<Permission>> pr = new PageResult<>();
        pr.setRecords(page.getRecords());
        pr.setCurrent(page.getCurrent());
        pr.setTotal(page.getTotal());
        pr.setSize(page.getSize());
        return Result.success(pr);
    }

    @SaCheckPermission("permission:add")
    @Operation(summary = "新增权限")
    @PostMapping("/add")
    public BaseResponse<Boolean> add(@RequestBody Permission permission) {
        return Result.success(permissionService.save(permission));
    }

    @SaCheckPermission("permission:del")
    @Operation(summary = "删除权限")
    @GetMapping("/del")
    public BaseResponse<Boolean> del(@RequestParam Integer id) {
        return Result.success(permissionService.removeById(id));
    }

    @SaCheckPermission("permission:del")
    @Operation(summary = "批量删除权限")
    @PostMapping("/batchDel")
    public BaseResponse<Boolean> batchDel(@RequestBody List<Integer> ids) {
        return Result.success(permissionService.removeByIds(ids));
    }

    @SaCheckPermission("permission:edit")
    @Operation(summary = "编辑权限")
    @PostMapping("/edit")
    public BaseResponse<Boolean> edit(@RequestBody Permission permission) {
        return Result.success(permissionService.updateById(permission));
    }
}
