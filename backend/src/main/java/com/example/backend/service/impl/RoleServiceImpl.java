package com.example.backend.service.impl;

import com.example.backend.entity.Role;
import com.example.backend.mapper.RoleMapper;
import com.example.backend.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>
        implements IRoleService {
}
