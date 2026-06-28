package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.backend.entity.Permission;
import com.example.backend.entity.RolePermission;
import com.example.backend.entity.UserRole;
import com.example.backend.mapper.PermissionMapper;
import com.example.backend.mapper.RolePermissionMapper;
import com.example.backend.mapper.UserRoleMapper;
import com.example.backend.service.IPermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission>
        implements IPermissionService {

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private RolePermissionMapper rolePermissionMapper;

    @Resource
    private PermissionMapper permissionMapper;

    @Override
    public List<String> getPermissionCodesByUserId(Integer userId) {
        List<Integer> roleIds = userRoleMapper.selectList(
                new QueryWrapper<UserRole>().eq("user_id", userId)
        ).stream().map(UserRole::getRoleId).collect(Collectors.toList());
        if (roleIds.isEmpty()) return Collections.emptyList();

        List<Integer> permIds = rolePermissionMapper.selectList(
                new QueryWrapper<RolePermission>().in("role_id", roleIds)
        ).stream().map(RolePermission::getPermissionId).distinct().collect(Collectors.toList());
        if (permIds.isEmpty()) return Collections.emptyList();

        return permissionMapper.selectList(
                new QueryWrapper<Permission>().in("id", permIds)
        ).stream().map(Permission::getPermissionCode).distinct().collect(Collectors.toList());
    }
}
