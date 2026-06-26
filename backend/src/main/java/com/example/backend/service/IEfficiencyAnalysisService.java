package com.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.common.model.PageResult;
import com.example.backend.entity.EfficiencyReport;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 效能分析 服务类
 * </p>
 *
 * @author
 * @since 2026-06-26
 */
public interface IEfficiencyAnalysisService extends IService<EfficiencyReport> {

    /**
     * 综合概览数据（资源总数、实训完成率、用户活跃度等）
     */
    Map<String, Object> getOverview();

    /**
     * 资源利用分析（各资源访问量/收藏量排名）
     */
    Map<String, Object> getResourceUsage();

    /**
     * 实训效能分析（实训完成率/平均时长）
     */
    Map<String, Object> getTrainingEfficiency();

    /**
     * 用户活跃度分析
     */
    Map<String, Object> getUserActivity();

    /**
     * 保存分析报表
     */
    Integer saveReport(EfficiencyReport request);

    /**
     * 报表列表
     */
    PageResult getReportPage(Integer pageNum, Integer pageSize, String reportType);

    /**
     * 查看报表详情
     */
    EfficiencyReport getReportById(Integer id);

    /**
     * 删除报表
     */
    Boolean delReport(Integer id);
}