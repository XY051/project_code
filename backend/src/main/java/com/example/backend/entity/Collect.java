package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 收藏表
 * </p>
 *
 * @author 
 * @since 2025-05-04
 */
@Getter
@Setter
@Schema(description = "Collect对象")
public class Collect implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description ="ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description ="用户ID")
    private Integer userId;

    @Schema(description ="文物ID")
    private Integer heritageId;

    @Schema(description ="创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @Schema(description ="更新时间")
    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}