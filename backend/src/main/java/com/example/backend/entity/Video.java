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
 * 宣传视频表
 * </p>
 *
 * @author 
 * @since 2025-01-20
 */
@Getter
@Setter
@Schema(description = "Video对象")
public class Video implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description ="ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description ="视频标题")
    private String title;

    @Schema(description ="视频地址")
    private String url;

    @Schema(description ="封面图")
    private String coverUrl;

    @Schema(description ="浏览次数")
    private Integer viewCount;

    @Schema(description ="创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @Schema(description ="更新时间")
    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}