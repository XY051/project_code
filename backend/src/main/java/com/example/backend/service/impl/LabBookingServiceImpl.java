 package com.example.backend.service.impl;
 
 import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
 import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
 import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
 import com.example.backend.common.enums.CodeEnum;
 import com.example.backend.common.model.PageResult;
 import com.example.backend.entity.LabBooking;
 import com.example.backend.exception.BusinessException;
 import com.example.backend.mapper.LabBookingMapper;
 import com.example.backend.service.ILabBookingService;
 import jakarta.annotation.Resource;
 import org.springframework.beans.BeanUtils;
 import org.springframework.stereotype.Service;
 import java.util.List;
 
 @Service
 public class LabBookingServiceImpl extends ServiceImpl<LabBookingMapper, LabBooking> implements ILabBookingService {
     @Resource
     private LabBookingMapper labBookingMapper;
 
     @Override
     public Integer add(LabBooking request) {
         LabBooking saveData = new LabBooking();
         BeanUtils.copyProperties(request, saveData);
         labBookingMapper.insert(saveData);
         return saveData.getId();
     }
 
     @Override
     public Boolean batchAdd(List<LabBooking> request) { return saveBatch(request); }
 
     @Override
     public Boolean del(Integer id) {
         if (id == null || id <= 0) throw new BusinessException(CodeEnum.PARAMS_ERROR);
         return removeById(id);
     }
 
     @Override
     public Boolean batchDel(List<Integer> ids) {
         if (ids == null || ids.isEmpty()) throw new BusinessException(CodeEnum.PARAMS_ERROR);
         return removeByIds(ids);
     }
 
     @Override
     public Boolean edit(Integer id, LabBooking request) {
         if (id == null || id <= 0) throw new BusinessException(CodeEnum.PARAMS_ERROR);
         LabBooking updateData = new LabBooking();
         BeanUtils.copyProperties(request, updateData);
         updateData.setId(id);
         return updateById(updateData);
     }
 
     @Override
     public PageResult page(Integer pageNum, Integer pageSize, Integer labId, Integer userId) {
         QueryWrapper<LabBooking> qw = new QueryWrapper<>();
         qw.orderByDesc("create_time");
         if (labId != null && labId > 0) qw.eq("lab_id", labId);
         if (userId != null && userId > 0) qw.eq("user_id", userId);
         Page<LabBooking> page = labBookingMapper.selectPage(new Page<>(pageNum, pageSize), qw);
         PageResult<List<LabBooking>> pr = new PageResult<>();
         pr.setRecords(page.getRecords());
         pr.setCurrent(page.getCurrent());
         pr.setTotal(page.getTotal());
         pr.setSize(page.getSize());
         return pr;
     }
 
     @Override
     public List<LabBooking> list() {
         return labBookingMapper.selectList(new QueryWrapper<LabBooking>().orderByDesc("create_time"));
     }
 }
