package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@TableName(value = "teaching_guidance")
@Data
public class TeachingGuidance implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer teacherId;

    private Integer studentId;

    private Integer trainingId;

    private String guidanceContent;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date guidanceTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
