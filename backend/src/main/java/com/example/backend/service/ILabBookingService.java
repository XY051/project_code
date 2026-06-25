 package com.example.backend.service;
 
 import com.baomidou.mybatisplus.extension.service.IService;
 import com.example.backend.common.model.PageResult;
 import com.example.backend.entity.LabBooking;
 import java.util.List;
 
 public interface ILabBookingService extends IService<LabBooking> {
     Integer add(LabBooking request);
     Boolean batchAdd(List<LabBooking> request);
     Boolean del(Integer id);
     Boolean batchDel(List<Integer> ids);
     Boolean edit(Integer id, LabBooking request);
     PageResult page(Integer pageNum, Integer pageSize, Integer labId, Integer userId);
     List<LabBooking> list();
 }
