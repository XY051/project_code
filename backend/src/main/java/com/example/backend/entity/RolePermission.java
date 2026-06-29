package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;

import lombok.Data;

@TableName(value = "role_permission")
@Data
public class RolePermission implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer roleId;

    private Integer permissionId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
