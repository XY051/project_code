package com.example.backend.controller;

import com.example.backend.common.model.PageResult;
import com.example.backend.common.result.BaseResponse;
import com.example.backend.common.result.Result;
import com.example.backend.entity.EfficiencyReport;
import com.example.backend.service.IEfficiencyAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.Map;

/**
 * @Description 效能分析控制层
 * @Version 1.0
 */
@Tag(name = "效能分析管理")
@RestController
@RequestMapping("/efficiencyAnalysis")
public class EfficiencyAnalysisController {

    @Resource
    private IEfficiencyAnalysisService efficiencyAnalysisService;

    @GetMapping("/overview")
    @Operation(summary = "综合概览数据")
    public BaseResponse<Map<String, Object>> overview() {
        Map<String, Object> res = efficiencyAnalysisService.getOverview();
        return Result.success(res);
    }

    @GetMapping("/resourceUsage")
    @Operation(summary = "资源利用分析")
    public BaseResponse<Map<String, Object>> resourceUsage() {
        Map<String, Object> res = efficiencyAnalysisService.getResourceUsage();
        return Result.success(res);
    }

    @GetMapping("/trainingEfficiency")
    @Operation(summary = "实训效能分析")
    public BaseResponse<Map<String, Object>> trainingEfficiency() {
        Map<String, Object> res = efficiencyAnalysisService.getTrainingEfficiency();
        return Result.success(res);
    }

    @GetMapping("/userActivity")
    @Operation(summary = "用户活跃度分析")
    public BaseResponse<Map<String, Object>> userActivity() {
        Map<String, Object> res = efficiencyAnalysisService.getUserActivity();
        return Result.success(res);
    }

    @PostMapping("/report/save")
    @Operation(summary = "保存分析报表")
    public BaseResponse<Integer> saveReport(@RequestBody EfficiencyReport request) {
        Integer res = efficiencyAnalysisService.saveReport(request);
        return Result.success(res);
    }

    @GetMapping("/report/page")
    @Operation(summary = "分页查询报表")
    public BaseResponse<PageResult> reportPage(@RequestParam(defaultValue = "1") Integer pageNum,
                                                @RequestParam(defaultValue = "10") Integer pageSize,
                                                @RequestParam(required = false) String reportType) {
        PageResult res = efficiencyAnalysisService.getReportPage(pageNum, pageSize, reportType);
        return Result.success(res);
    }

    @GetMapping("/report/getById")
    @Operation(summary = "查看报表详情")
    public BaseResponse<EfficiencyReport> reportGetById(@RequestParam Integer id) {
        EfficiencyReport res = efficiencyAnalysisService.getReportById(id);
        return Result.success(res);
    }

    @GetMapping("/report/del")
    @Operation(summary = "删除报表")
    public BaseResponse<Boolean> reportDel(@RequestParam Integer id) {
        Boolean res = efficiencyAnalysisService.delReport(id);
        return Result.success(res);
    }
}