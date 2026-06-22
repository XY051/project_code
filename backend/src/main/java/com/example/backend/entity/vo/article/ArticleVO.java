package com.example.backend.entity.vo.article;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * <p>
 * 资讯表 视图对象
 * </p>
 *
 * @author 
 * @since 2025-01-20
 */
@Data
@Schema(description = "ArticleVO对象")
public class ArticleVO {

    @Schema(description ="ID")
    private Integer id;

    @Schema(description ="标题")
    private String title;

    @Schema(description = "简介")
    private String intro;

    @Schema(description ="内容")
    private String content;

    @Schema(description ="浏览次数")
    private Integer viewCount;

    @Schema(description ="封面图片URL")
    private String coverUrl;

    @Schema(description ="创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @Schema(description ="更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}