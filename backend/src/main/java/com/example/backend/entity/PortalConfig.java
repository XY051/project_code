package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 门户配置表
 * </p>
 *
 * @author
 * @since 2026-06-26
 */
@Getter
@Setter
@Schema(description = "PortalConfig对象")
public class PortalConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "配置键")
    private String configKey;

    @Schema(description = "配置值")
    private String configValue;

    @Schema(description = "配置类型：text-文本, image-图片, switch-开关, json-JSON数据")
    private String configType;

    @Schema(description = "配置说明")
    private String remark;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @Schema(description = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}