package com.example.backend.service;

import com.example.backend.entity.Permission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IPermissionService extends IService<Permission> {
    List<String> getPermissionCodesByUserId(Integer userId);
}
