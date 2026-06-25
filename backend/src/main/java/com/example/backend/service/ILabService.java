 package com.example.backend.service;
 
 import com.baomidou.mybatisplus.extension.service.IService;
 import com.example.backend.common.model.PageResult;
 import com.example.backend.entity.Lab;
 import java.util.List;
 
 public interface ILabService extends IService<Lab> {
     Integer add(Lab request);
     Boolean batchAdd(List<Lab> request);
     Boolean del(Integer id);
     Boolean batchDel(List<Integer> ids);
     Boolean edit(Integer id, Lab request);
     PageResult page(Integer pageNum, Integer pageSize, String name);
     List<Lab> list();
 }
