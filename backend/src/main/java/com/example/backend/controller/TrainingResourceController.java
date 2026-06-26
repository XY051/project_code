package com.example.backend.controller;

import com.example.backend.common.model.PageResult;
import com.example.backend.common.result.BaseResponse;
import com.example.backend.common.result.Result;
import com.example.backend.entity.TrainingResource;
import com.example.backend.entity.vo.trainingresource.TrainingResourceVO;
import com.example.backend.service.ITrainingResourceService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trainingResource")
public class TrainingResourceController {

    @Resource
    private ITrainingResourceService trainingResourceService;

    @PostMapping("/add")
    public BaseResponse<Integer> add(@RequestBody TrainingResource request) {
        return Result.success(trainingResourceService.add(request));
    }

    @PostMapping("/batchAdd")
    public BaseResponse<Boolean> batchAdd(@RequestBody List<TrainingResource> request) {
        return Result.success(trainingResourceService.batchAdd(request));
    }

    @GetMapping("/del")
    public BaseResponse<Boolean> del(@RequestParam Integer id) {
        return Result.success(trainingResourceService.del(id));
    }

    @PostMapping("/batchDel")
    public BaseResponse<Boolean> batchDel(@RequestBody List<Integer> ids) {
        return Result.success(trainingResourceService.batchDel(ids));
    }

    @PostMapping("/edit")
    public BaseResponse<Boolean> edit(@RequestParam Integer id, @RequestBody TrainingResource request) {
        return Result.success(trainingResourceService.edit(id, request));
    }

    @GetMapping("/page")
    public BaseResponse<PageResult<List<TrainingResourceVO>>> page(@RequestParam Integer pageNum,
                                                                   @RequestParam Integer pageSize,
                                                                   @RequestParam(required = false) String name,
                                                                   @RequestParam(required = false) Integer categoryId,
                                                                   @RequestParam(required = false) String resourceType,
                                                                   @RequestParam(required = false) String openType) {
        return Result.success(trainingResourceService.queryPage(pageNum, pageSize, name, categoryId, resourceType, openType));
    }

    @GetMapping("/list")
    public BaseResponse<List<TrainingResource>> list() {
        return Result.success(trainingResourceService.getAll());
    }

    @GetMapping("/getById")
    public BaseResponse<TrainingResource> getById(@RequestParam Integer id) {
        return Result.success(trainingResourceService.getByIdDetail(id));
    }

    @GetMapping("/hot")
    public BaseResponse<PageResult<List<TrainingResourceVO>>> hot(@RequestParam(defaultValue = "1") Integer pageNum,
                                                                  @RequestParam(defaultValue = "8") Integer pageSize,
                                                                  @RequestParam(defaultValue = "view_count") String orderBy,
                                                                  @RequestParam(defaultValue = "desc") String orderType) {
        return Result.success(trainingResourceService.getHotPage(pageNum, pageSize, orderBy, orderType));
    }
}
