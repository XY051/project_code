 package com.example.backend.service;
 
 import com.baomidou.mybatisplus.extension.service.IService;
 import com.example.backend.common.model.PageResult;
 import com.example.backend.entity.Equipment;
 import java.util.List;
 
 public interface IEquipmentService extends IService<Equipment> {
     Integer add(Equipment request);
     Boolean batchAdd(List<Equipment> request);
     Boolean del(Integer id);
     Boolean batchDel(List<Integer> ids);
     Boolean edit(Integer id, Equipment request);
     PageResult page(Integer pageNum, Integer pageSize, String name, Integer labId);
     List<Equipment> list();
 }
