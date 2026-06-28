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
 * 效能分析报表表
 * </p>
 *
 * @author
 * @since 2026-06-26
 */
@Getter
@Setter
@Schema(description = "EfficiencyReport对象")
public class EfficiencyReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "报表名称")
    private String reportName;

    @Schema(description = "报表类型：resource-资源利用, training-实训效能, user-用户活跃, comprehensive-综合分析")
    private String reportType;

    @Schema(description = "报表数据JSON")
    private String dataJson;

    @Schema(description = "图表类型：bar-柱状图, line-折线图, pie-饼图, radar-雷达图")
    private String chartType;

    @Schema(description = "创建用户ID")
    private Integer createUserId;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @Schema(description = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}