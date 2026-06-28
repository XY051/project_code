package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;

import lombok.Data;

@TableName(value = "user_role")
@Data
public class UserRole implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private Integer roleId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
