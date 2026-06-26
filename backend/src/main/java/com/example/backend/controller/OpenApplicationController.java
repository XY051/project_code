package com.example.backend.controller;

import com.example.backend.common.model.PageResult;
import com.example.backend.common.result.BaseResponse;
import com.example.backend.common.result.Result;
import com.example.backend.entity.OpenApplication;
import com.example.backend.service.IOpenApplicationService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/openApplication")
public class OpenApplicationController {

    @Resource
    private IOpenApplicationService openApplicationService;

    @PostMapping("/add")
    public BaseResponse<Integer> add(@RequestBody OpenApplication request) {
        return Result.success(openApplicationService.add(request));
    }

    @GetMapping("/del")
    public BaseResponse<Boolean> del(@RequestParam Integer id) {
        return Result.success(openApplicationService.del(id));
    }

    @PostMapping("/batchDel")
    public BaseResponse<Boolean> batchDel(@RequestBody List<Integer> ids) {
        return Result.success(openApplicationService.batchDel(ids));
    }

    @PostMapping("/edit")
    public BaseResponse<Boolean> edit(@RequestParam Integer id, @RequestBody OpenApplication request) {
        return Result.success(openApplicationService.edit(id, request));
    }

    @PostMapping("/audit")
    public BaseResponse<Boolean> audit(@RequestParam Integer id,
                                       @RequestParam String auditStatus,
                                       @RequestParam(required = false) String auditComment) {
        return Result.success(openApplicationService.audit(id, auditStatus, auditComment));
    }

    @PostMapping("/return")
    public BaseResponse<Boolean> returnResource(@RequestParam Integer id) {
        return Result.success(openApplicationService.returnResource(id));
    }

    @GetMapping("/page")
    public BaseResponse<PageResult<List<OpenApplication>>> page(@RequestParam Integer pageNum,
                                                               @RequestParam Integer pageSize,
                                                               @RequestParam(required = false) String resourceName,
                                                               @RequestParam(required = false) Integer resourceId,
                                                               @RequestParam(required = false) String auditStatus,
                                                               @RequestParam(required = false) String useStatus,
                                                               @RequestParam(required = false) Integer userId) {
        return Result.success(openApplicationService.queryPage(pageNum, pageSize, resourceName, resourceId, auditStatus, useStatus, userId));
    }

    @GetMapping("/list")
    public BaseResponse<List<OpenApplication>> list() {
        return Result.success(openApplicationService.getAll());
    }
}
