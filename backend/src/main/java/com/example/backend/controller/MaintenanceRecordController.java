package com.example.backend.controller;

import com.example.backend.common.model.PageResult;
import com.example.backend.common.result.BaseResponse;
import com.example.backend.common.result.Result;
import com.example.backend.entity.MaintenanceRecord;
import com.example.backend.service.IMaintenanceRecordService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/maintenanceRecord")
public class MaintenanceRecordController {
    @Resource
    private IMaintenanceRecordService maintenanceRecordService;

    @PostMapping("/add")
    public BaseResponse<Integer> add(@RequestBody MaintenanceRecord request) {
        return Result.success(maintenanceRecordService.add(request));
    }

    @PostMapping("/batchAdd")
    public BaseResponse<Boolean> batchAdd(@RequestBody List<MaintenanceRecord> request) {
        return Result.success(maintenanceRecordService.batchAdd(request));
    }

    @GetMapping("/del")
    public BaseResponse<Boolean> del(@RequestParam Integer id) {
        return Result.success(maintenanceRecordService.del(id));
    }

    @PostMapping("/batchDel")
    public BaseResponse<Boolean> batchDel(@RequestBody List<Integer> ids) {
        return Result.success(maintenanceRecordService.batchDel(ids));
    }

    @PostMapping("/edit")
    public BaseResponse<Boolean> edit(@RequestParam Integer id, @RequestBody MaintenanceRecord request) {
        return Result.success(maintenanceRecordService.edit(id, request));
    }

    @GetMapping("/page")
    public BaseResponse<PageResult<List<MaintenanceRecord>>> page(
            @RequestParam Integer pageNum, @RequestParam Integer pageSize,
            @RequestParam(required = false) Integer equipmentId) {
        return Result.success(maintenanceRecordService.page(pageNum, pageSize, equipmentId));
    }

    @GetMapping("/list")
    public BaseResponse<List<MaintenanceRecord>> list() {
        return Result.success(maintenanceRecordService.list());
    }

    @GetMapping("/getById")
    public BaseResponse<MaintenanceRecord> getById(@RequestParam Integer id) {
        return Result.success(maintenanceRecordService.getById(id));
    }
}

