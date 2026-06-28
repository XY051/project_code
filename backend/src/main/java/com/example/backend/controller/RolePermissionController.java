package com.example.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.backend.common.result.BaseResponse;
import com.example.backend.common.result.Result;
import com.example.backend.entity.RolePermission;
import com.example.backend.mapper.RolePermissionMapper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rolePermission")
public class RolePermissionController {
    @Resource
    private RolePermissionMapper rolePermissionMapper;

    @Operation(summary = "根据角色ID获取权限ID列表")
    @GetMapping("/listByRoleId")
    public BaseResponse<List<Integer>> listByRoleId(@RequestParam Integer roleId) {
        List<Integer> permIds = rolePermissionMapper.selectList(
                new QueryWrapper<RolePermission>().eq("role_id", roleId)
        ).stream().map(RolePermission::getPermissionId).collect(Collectors.toList());
        return Result.success(permIds);
    }

    @Operation(summary = "分配角色权限")
    @PostMapping("/assign")
    @Transactional
    public BaseResponse<Boolean> assign(@RequestBody Map<String, Object> body) {
        Integer roleId = (Integer) body.get("roleId");
        @SuppressWarnings("unchecked")
        List<Integer> permissionIds = (List<Integer>) body.get("permissionIds");
        // 删除旧关联
        rolePermissionMapper.delete(new QueryWrapper<RolePermission>().eq("role_id", roleId));
        // 插入新关联
        for (Integer permId : permissionIds) {
            RolePermission rp = new RolePermission();
            rp.setRoleId(roleId);
            rp.setPermissionId(permId);
            rolePermissionMapper.insert(rp);
        }
        return Result.success(true);
    }
}
