package com.example.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.common.model.PageResult;
import com.example.backend.common.result.BaseResponse;
import com.example.backend.common.result.Result;
import com.example.backend.entity.TeachingGuidance;
import com.example.backend.service.ITeachingGuidanceService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teachingGuidance")
public class TeachingGuidanceController {
    @Resource
    private ITeachingGuidanceService teachingGuidanceService;

    @Operation(summary = "查询所有指导记录")
    @GetMapping("/list")
    public BaseResponse<List<TeachingGuidance>> list() {
        return Result.success(teachingGuidanceService.list());
    }

    @Operation(summary = "分页查询指导记录")
    @GetMapping("/page")
    public BaseResponse<PageResult<List<TeachingGuidance>>> page(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) Integer teacherId,
            @RequestParam(required = false) Integer studentId) {
        QueryWrapper<TeachingGuidance> qw = new QueryWrapper<>();
        if (teacherId != null) qw.eq("teacher_id", teacherId);
        if (studentId != null) qw.eq("student_id", studentId);
        qw.orderByDesc("id");
        Page<TeachingGuidance> page = teachingGuidanceService.page(new Page<>(pageNum, pageSize), qw);
        PageResult<List<TeachingGuidance>> pr = new PageResult<>();
        pr.setRecords(page.getRecords());
        pr.setCurrent(page.getCurrent());
        pr.setTotal(page.getTotal());
        pr.setSize(page.getSize());
        return Result.success(pr);
    }

    @Operation(summary = "新增指导记录")
    @PostMapping("/add")
    public BaseResponse<Boolean> add(@RequestBody TeachingGuidance record) {
        return Result.success(teachingGuidanceService.save(record));
    }

    @Operation(summary = "删除指导记录")
    @GetMapping("/del")
    public BaseResponse<Boolean> del(@RequestParam Integer id) {
        return Result.success(teachingGuidanceService.removeById(id));
    }

    @Operation(summary = "批量删除指导记录")
    @PostMapping("/batchDel")
    public BaseResponse<Boolean> batchDel(@RequestBody List<Integer> ids) {
        return Result.success(teachingGuidanceService.removeByIds(ids));
    }

    @Operation(summary = "编辑指导记录")
    @PostMapping("/edit")
    public BaseResponse<Boolean> edit(@RequestBody TeachingGuidance record) {
        return Result.success(teachingGuidanceService.updateById(record));
    }
}
