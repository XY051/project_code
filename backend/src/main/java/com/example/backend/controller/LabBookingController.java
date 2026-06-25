 package com.example.backend.controller;
 
 import com.example.backend.common.model.PageResult;
 import com.example.backend.common.result.BaseResponse;
 import com.example.backend.common.result.Result;
 import com.example.backend.entity.LabBooking;
 import com.example.backend.service.ILabBookingService;
 import io.swagger.v3.oas.annotations.Operation;
 import jakarta.annotation.Resource;
 import org.springframework.web.bind.annotation.*;
 import java.util.List;
 
 @RestController
 @RequestMapping("/labBooking")
 public class LabBookingController {
     @Resource
     private ILabBookingService labBookingService;
 
     @PostMapping("/add")
     public BaseResponse<Integer> add(@RequestBody LabBooking request) {
         return Result.success(labBookingService.add(request));
     }
 
     @GetMapping("/del")
     public BaseResponse<Boolean> del(@RequestParam Integer id) {
         return Result.success(labBookingService.del(id));
     }
 
     @PostMapping("/batchDel")
     public BaseResponse<Boolean> batchDel(@RequestBody List<Integer> ids) {
         return Result.success(labBookingService.batchDel(ids));
     }
 
     @PostMapping("/edit")
     public BaseResponse<Boolean> edit(@RequestParam Integer id, @RequestBody LabBooking request) {
         return Result.success(labBookingService.edit(id, request));
     }
 
     @GetMapping("/page")
     public BaseResponse<PageResult<List<LabBooking>>> page(
             @RequestParam Integer pageNum, @RequestParam Integer pageSize,
             @RequestParam(required = false) Integer labId,
             @RequestParam(required = false) Integer userId) {
         return Result.success(labBookingService.page(pageNum, pageSize, labId, userId));
     }
 
     @GetMapping("/list")
     public BaseResponse<List<LabBooking>> list() {
         return Result.success(labBookingService.list());
     }
 
     @GetMapping("/getById")
     @Operation(summary = "根据ID查询预约")
     public BaseResponse<LabBooking> getById(@RequestParam Integer id) {
         return Result.success(labBookingService.getById(id));
     }
 }
