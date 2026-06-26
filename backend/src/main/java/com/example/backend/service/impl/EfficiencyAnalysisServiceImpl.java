package com.example.backend.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.common.enums.CodeEnum;
import com.example.backend.common.model.PageResult;
import com.example.backend.entity.*;
import com.example.backend.exception.BusinessException;
import com.example.backend.mapper.EfficiencyReportMapper;
import com.example.backend.service.*;
import com.example.backend.utils.UserUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.*;

/**
 * <p>
 * 效能分析 服务实现类
 * </p>
 *
 * @author
 * @since 2026-06-26
 */
@Service
public class EfficiencyAnalysisServiceImpl extends ServiceImpl<EfficiencyReportMapper, EfficiencyReport> implements IEfficiencyAnalysisService {

    @Resource
    private EfficiencyReportMapper efficiencyReportMapper;

    /**
     * 综合概览数据
     * 注意：依赖第2人的 TrainingResource 和第3人的 TrainingProject/ExperimentRecord，
     * 当前先使用 CulturalHeritage/Activity/ActivityApplication 等原表名做查询，
     * 待相关模块改造后再更新表名
     */
    @Override
    public Map<String, Object> getOverview() {
        Map<String, Object> result = new HashMap<>();

        // TODO: 以下count查询依赖第2/3人改造后的表，当前暂用原表名
        // 资源总数（暂用cultural_heritage表，等第2人改training_resource后更新）
        // 实训项目数（暂用activity表，等第3人改training_project后更新）
        // 用户总数
        // 这些数据将在联调阶段适配

        result.put("resourceCount", 0);
        result.put("trainingCount", 0);
        result.put("userCount", 0);
        result.put("completionRate", 0);
        result.put("activeUserCount", 0);
        result.put("updateTime", new Date());

        return result;
    }

    @Override
    public Map<String, Object> getResourceUsage() {
        Map<String, Object> result = new HashMap<>();
        List<String> names = new ArrayList<>();
        List<Integer> viewCounts = new ArrayList<>();
        List<Integer> collectCounts = new ArrayList<>();

        // TODO: 第2人改造Resource后适配
        result.put("names", names);
        result.put("viewCounts", viewCounts);
        result.put("collectCounts", collectCounts);

        return result;
    }

    @Override
    public Map<String, Object> getTrainingEfficiency() {
        Map<String, Object> result = new HashMap<>();

        // TODO: 第3人改造Experiment后适配
        result.put("completionRate", 0);
        result.put("avgDuration", 0);
        result.put("totalExperiments", 0);
        result.put("completedCount", 0);

        return result;
    }

    @Override
    public Map<String, Object> getUserActivity() {
        Map<String, Object> result = new HashMap<>();

        // TODO: 可基于现有User表统计
        result.put("totalUsers", 0);
        result.put("activeUsers", 0);
        result.put("newUsersToday", 0);
        result.put("activityTrend", new ArrayList<>());

        return result;
    }

    @Override
    public Integer saveReport(EfficiencyReport request) {
        EfficiencyReport saveData = new EfficiencyReport();
        BeanUtils.copyProperties(request, saveData);
        // 设置创建用户ID
        try {
            User currentUser = UserUtils.getUserInfo();
            if (currentUser != null) {
                saveData.setCreateUserId(currentUser.getId());
            }
        } catch (Exception ignored) {
            // 未登录时不设置
        }
        efficiencyReportMapper.insert(saveData);
        return saveData.getId();
    }

    @Override
    public PageResult getReportPage(Integer pageNum, Integer pageSize, String reportType) {
        QueryWrapper<EfficiencyReport> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        if (CharSequenceUtil.isNotBlank(reportType)) {
            queryWrapper.eq("report_type", reportType);
        }
        Page<EfficiencyReport> page = efficiencyReportMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
        PageResult<List<EfficiencyReport>> pageResult = new PageResult<>();
        pageResult.setRecords(page.getRecords());
        pageResult.setCurrent(page.getCurrent());
        pageResult.setTotal(page.getTotal());
        pageResult.setSize(page.getSize());
        return pageResult;
    }

    @Override
    public EfficiencyReport getReportById(Integer id) {
        if (id == null || id <= 0) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR);
        }
        return efficiencyReportMapper.selectById(id);
    }

    @Override
    public Boolean delReport(Integer id) {
        if (id == null || id <= 0) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR);
        }
        return removeById(id);
    }
}