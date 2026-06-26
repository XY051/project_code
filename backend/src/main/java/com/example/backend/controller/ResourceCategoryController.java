package com.example.backend.controller;

import com.example.backend.common.model.PageResult;
import com.example.backend.common.result.BaseResponse;
import com.example.backend.common.result.Result;
import com.example.backend.entity.ResourceCategory;
import com.example.backend.service.IResourceCategoryService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resourceCategory")
public class ResourceCategoryController {

    @Resource
    private IResourceCategoryService resourceCategoryService;

    @PostMapping("/add")
    public BaseResponse<Integer> add(@RequestBody ResourceCategory request) {
        return Result.success(resourceCategoryService.add(request));
    }

    @PostMapping("/batchAdd")
    public BaseResponse<Boolean> batchAdd(@RequestBody List<ResourceCategory> request) {
        return Result.success(resourceCategoryService.batchAdd(request));
    }

    @GetMapping("/del")
    public BaseResponse<Boolean> del(@RequestParam Integer id) {
        return Result.success(resourceCategoryService.del(id));
    }

    @PostMapping("/batchDel")
    public BaseResponse<Boolean> batchDel(@RequestBody List<Integer> ids) {
        return Result.success(resourceCategoryService.batchDel(ids));
    }

    @PostMapping("/edit")
    public BaseResponse<Boolean> edit(@RequestParam Integer id, @RequestBody ResourceCategory request) {
        return Result.success(resourceCategoryService.edit(id, request));
    }

    @GetMapping("/page")
    public BaseResponse<PageResult<List<ResourceCategory>>> page(@RequestParam Integer pageNum,
                                                                 @RequestParam Integer pageSize,
                                                                 @RequestParam(required = false) String name) {
        return Result.success(resourceCategoryService.queryPage(pageNum, pageSize, name));
    }

    @GetMapping("/list")
    public BaseResponse<List<ResourceCategory>> list() {
        return Result.success(resourceCategoryService.getAll());
    }

    @GetMapping("/getById")
    public BaseResponse<ResourceCategory> getById(@RequestParam Integer id) {
        return Result.success(resourceCategoryService.getByIdDetail(id));
    }
}
