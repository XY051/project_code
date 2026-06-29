package com.example.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.common.model.PageResult;
import com.example.backend.common.result.BaseResponse;
import com.example.backend.common.result.Result;
import com.example.backend.entity.MonitorRecord;
import com.example.backend.service.IMonitorRecordService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/monitorRecord")
public class MonitorRecordController {
    @Resource
    private IMonitorRecordService monitorRecordService;

    @Operation(summary = "查询所有监控记录")
    @GetMapping("/list")
    public BaseResponse<List<MonitorRecord>> list() {
        return Result.success(monitorRecordService.list());
    }

    @Operation(summary = "分页查询监控记录")
    @GetMapping("/page")
    public BaseResponse<PageResult<List<MonitorRecord>>> page(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer trainingId,
            @RequestParam(required = false) Integer status) {
        QueryWrapper<MonitorRecord> qw = new QueryWrapper<>();
        if (userId != null) qw.eq("user_id", userId);
        if (trainingId != null) qw.eq("training_id", trainingId);
        if (status != null) qw.eq("status", status);
        qw.orderByDesc("id");
        Page<MonitorRecord> page = monitorRecordService.page(new Page<>(pageNum, pageSize), qw);
        PageResult<List<MonitorRecord>> pr = new PageResult<>();
        pr.setRecords(page.getRecords());
        pr.setCurrent(page.getCurrent());
        pr.setTotal(page.getTotal());
        pr.setSize(page.getSize());
        return Result.success(pr);
    }

    @Operation(summary = "新增监控记录")
    @PostMapping("/add")
    public BaseResponse<Boolean> add(@RequestBody MonitorRecord record) {
        return Result.success(monitorRecordService.save(record));
    }

    @Operation(summary = "删除监控记录")
    @GetMapping("/del")
    public BaseResponse<Boolean> del(@RequestParam Integer id) {
        return Result.success(monitorRecordService.removeById(id));
    }

    @Operation(summary = "批量删除监控记录")
    @PostMapping("/batchDel")
    public BaseResponse<Boolean> batchDel(@RequestBody List<Integer> ids) {
        return Result.success(monitorRecordService.removeByIds(ids));
    }

    @Operation(summary = "编辑监控记录")
    @PostMapping("/edit")
    public BaseResponse<Boolean> edit(@RequestBody MonitorRecord record) {
        return Result.success(monitorRecordService.updateById(record));
    }
}
