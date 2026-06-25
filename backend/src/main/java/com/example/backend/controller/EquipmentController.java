 package com.example.backend.controller;
 
 import com.example.backend.common.model.PageResult;
 import com.example.backend.common.result.BaseResponse;
 import com.example.backend.common.result.Result;
 import com.example.backend.entity.Equipment;
 import com.example.backend.service.IEquipmentService;
 import io.swagger.v3.oas.annotations.Operation;
 import jakarta.annotation.Resource;
 import org.springframework.web.bind.annotation.*;
 import java.util.List;
 
 @RestController
 @RequestMapping("/equipment")
 public class EquipmentController {
     @Resource
     private IEquipmentService equipmentService;
 
     @PostMapping("/add")
     public BaseResponse<Integer> add(@RequestBody Equipment request) {
         return Result.success(equipmentService.add(request));
     }
 
     @PostMapping("/batchAdd")
     public BaseResponse<Boolean> batchAdd(@RequestBody List<Equipment> request) {
         return Result.success(equipmentService.batchAdd(request));
     }
 
     @GetMapping("/del")
     public BaseResponse<Boolean> del(@RequestParam Integer id) {
         return Result.success(equipmentService.del(id));
     }
 
     @PostMapping("/batchDel")
     public BaseResponse<Boolean> batchDel(@RequestBody List<Integer> ids) {
         return Result.success(equipmentService.batchDel(ids));
     }
 
     @PostMapping("/edit")
     public BaseResponse<Boolean> edit(@RequestParam Integer id, @RequestBody Equipment request) {
         return Result.success(equipmentService.edit(id, request));
     }
 
     @GetMapping("/page")
     public BaseResponse<PageResult<List<Equipment>>> page(
             @RequestParam Integer pageNum, @RequestParam Integer pageSize,
             @RequestParam(required = false) String name,
             @RequestParam(required = false) Integer labId) {
         return Result.success(equipmentService.page(pageNum, pageSize, name, labId));
     }
 
     @GetMapping("/list")
     public BaseResponse<List<Equipment>> list() {
         return Result.success(equipmentService.list());
     }
 
     @GetMapping("/getById")
     public BaseResponse<Equipment> getById(@RequestParam Integer id) {
         return Result.success(equipmentService.getById(id));
     }
 }
