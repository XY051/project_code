 package com.example.backend.controller;
 
 import com.example.backend.common.model.PageResult;
 import com.example.backend.common.result.BaseResponse;
 import com.example.backend.common.result.Result;
 import com.example.backend.entity.Lab;
 import com.example.backend.service.ILabService;
 import io.swagger.v3.oas.annotations.Operation;
 import jakarta.annotation.Resource;
 import org.springframework.web.bind.annotation.*;
 import java.util.List;
 
 @RestController
 @RequestMapping("/lab")
 public class LabController {
     @Resource
     private ILabService labService;
 
     @PostMapping("/add")
     public BaseResponse<Integer> add(@RequestBody Lab request) {
         return Result.success(labService.add(request));
     }
 
     @PostMapping("/batchAdd")
     public BaseResponse<Boolean> batchAdd(@RequestBody List<Lab> request) {
         return Result.success(labService.batchAdd(request));
     }
 
     @GetMapping("/del")
     public BaseResponse<Boolean> del(@RequestParam Integer id) {
         return Result.success(labService.del(id));
     }
 
     @PostMapping("/batchDel")
     public BaseResponse<Boolean> batchDel(@RequestBody List<Integer> ids) {
         return Result.success(labService.batchDel(ids));
     }
 
     @PostMapping("/edit")
     public BaseResponse<Boolean> edit(@RequestParam Integer id, @RequestBody Lab request) {
         return Result.success(labService.edit(id, request));
     }
 
     @GetMapping("/page")
     public BaseResponse<PageResult<List<Lab>>> page(
             @RequestParam Integer pageNum, @RequestParam Integer pageSize,
             @RequestParam(required = false) String name) {
         return Result.success(labService.page(pageNum, pageSize, name));
     }
 
     @GetMapping("/list")
     public BaseResponse<List<Lab>> list() {
         return Result.success(labService.list());
     }
 
     @GetMapping("/getById")
     public BaseResponse<Lab> getById(@RequestParam Integer id) {
         return Result.success(labService.getById(id));
     }
 }
