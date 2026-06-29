package com.example.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.backend.common.result.BaseResponse;
import com.example.backend.common.result.Result;
import com.example.backend.entity.UserRole;
import com.example.backend.mapper.UserRoleMapper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/userRole")
public class UserRoleController {
    @Resource
    private UserRoleMapper userRoleMapper;

    @Operation(summary = "根据用户ID获取角色ID列表")
    @GetMapping("/listByUserId")
    public BaseResponse<List<Integer>> listByUserId(@RequestParam Integer userId) {
        List<Integer> roleIds = userRoleMapper.selectList(
                new QueryWrapper<UserRole>().eq("user_id", userId)
        ).stream().map(UserRole::getRoleId).collect(Collectors.toList());
        return Result.success(roleIds);
    }

    @Operation(summary = "分配用户角色")
    @PostMapping("/assign")
    @Transactional
    public BaseResponse<Boolean> assign(@RequestBody Map<String, Object> body) {
        Integer userId = (Integer) body.get("userId");
        @SuppressWarnings("unchecked")
        List<Integer> roleIds = (List<Integer>) body.get("roleIds");
        // 删除旧关联
        userRoleMapper.delete(new QueryWrapper<UserRole>().eq("user_id", userId));
        // 插入新关联
        for (Integer roleId : roleIds) {
            UserRole ur = new UserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            userRoleMapper.insert(ur);
        }
        return Result.success(true);
    }
}
