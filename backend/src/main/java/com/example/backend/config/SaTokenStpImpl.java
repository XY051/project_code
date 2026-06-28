package com.example.backend.config;

import cn.dev33.satoken.stp.StpInterface;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.backend.entity.Permission;
import com.example.backend.entity.Role;
import com.example.backend.entity.RolePermission;
import com.example.backend.entity.UserRole;
import com.example.backend.mapper.PermissionMapper;
import com.example.backend.mapper.RoleMapper;
import com.example.backend.mapper.RolePermissionMapper;
import com.example.backend.mapper.UserRoleMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SaTokenStpImpl implements StpInterface {

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private RolePermissionMapper rolePermissionMapper;

    @Resource
    private PermissionMapper permissionMapper;

    @Resource
    private RoleMapper roleMapper;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        Integer userId = Integer.parseInt(loginId.toString());
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

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Integer userId = Integer.parseInt(loginId.toString());
        List<Integer> roleIds = userRoleMapper.selectList(
                new QueryWrapper<UserRole>().eq("user_id", userId)
        ).stream().map(UserRole::getRoleId).collect(Collectors.toList());
        if (roleIds.isEmpty()) return Collections.emptyList();

        return roleMapper.selectList(
                new QueryWrapper<Role>().in("id", roleIds)
        ).stream().map(Role::getRoleCode).collect(Collectors.toList());
    }
}
