package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("training_resource")
@Schema(description = "虚拟仿真资源")
public class TrainingResource implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "资源名称")
    private String name;

    @Schema(description = "资源分类ID")
    private Integer categoryId;

    @Schema(description = "资源类型")
    private String resourceType;

    @Schema(description = "开放类型")
    private String openType;

    @Schema(description = "资源等级")
    private String resourceLevel;

    @Schema(description = "封面图片")
    private String coverImage;

    @Schema(description = "资源简介")
    private String intro;

    @Schema(description = "资源说明")
    private String description;

    @Schema(description = "实验入口链接")
    private String accessUrl;

    @Schema(description = "可用数量")
    private Integer availableCount;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "浏览次数")
    private Integer viewCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    private Date updateTime;
}
