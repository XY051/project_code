package com.example.backend.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.common.model.PageResult;
import com.example.backend.common.result.BaseResponse;
import com.example.backend.common.result.Result;
import com.example.backend.entity.Role;
import com.example.backend.service.IRoleService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {
    @Resource
    private IRoleService roleService;

    @Operation(summary = "查询所有角色")
    @GetMapping("/list")
    public BaseResponse<List<Role>> list() {
        return Result.success(roleService.list());
    }

    @Operation(summary = "分页查询角色")
    @GetMapping("/page")
    public BaseResponse<PageResult<List<Role>>> page(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) String roleName) {
        QueryWrapper<Role> qw = new QueryWrapper<>();
        if (roleName != null && !roleName.isEmpty()) {
            qw.like("role_name", roleName);
        }
        qw.orderByDesc("id");
        Page<Role> page = roleService.page(new Page<>(pageNum, pageSize), qw);
        PageResult<List<Role>> pr = new PageResult<>();
        pr.setRecords(page.getRecords());
        pr.setCurrent(page.getCurrent());
        pr.setTotal(page.getTotal());
        pr.setSize(page.getSize());
        return Result.success(pr);
    }

    @SaCheckPermission("role:add")
    @Operation(summary = "新增角色")
    @PostMapping("/add")
    public BaseResponse<Boolean> add(@RequestBody Role role) {
        return Result.success(roleService.save(role));
    }

    @SaCheckPermission("role:del")
    @Operation(summary = "删除角色")
    @GetMapping("/del")
    public BaseResponse<Boolean> del(@RequestParam Integer id) {
        return Result.success(roleService.removeById(id));
    }

    @SaCheckPermission("role:del")
    @Operation(summary = "批量删除角色")
    @PostMapping("/batchDel")
    public BaseResponse<Boolean> batchDel(@RequestBody List<Integer> ids) {
        return Result.success(roleService.removeByIds(ids));
    }

    @SaCheckPermission("role:edit")
    @Operation(summary = "编辑角色")
    @PostMapping("/edit")
    public BaseResponse<Boolean> edit(@RequestBody Role role) {
        return Result.success(roleService.updateById(role));
    }
}
