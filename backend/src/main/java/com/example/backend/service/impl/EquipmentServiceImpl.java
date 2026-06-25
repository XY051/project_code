 package com.example.backend.service.impl;
 
 import cn.hutool.core.text.CharSequenceUtil;
 import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
 import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
 import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
 import com.example.backend.common.enums.CodeEnum;
 import com.example.backend.common.model.PageResult;
 import com.example.backend.entity.Equipment;
 import com.example.backend.exception.BusinessException;
 import com.example.backend.mapper.EquipmentMapper;
 import com.example.backend.service.IEquipmentService;
 import jakarta.annotation.Resource;
 import org.springframework.beans.BeanUtils;
 import org.springframework.stereotype.Service;
 import java.util.List;
 
 @Service
 public class EquipmentServiceImpl extends ServiceImpl<EquipmentMapper, Equipment> implements IEquipmentService {
     @Resource
     private EquipmentMapper equipmentMapper;
 
     @Override
     public Integer add(Equipment request) {
         Equipment saveData = new Equipment();
         BeanUtils.copyProperties(request, saveData);
         equipmentMapper.insert(saveData);
         return saveData.getId();
     }
 
     @Override
     public Boolean batchAdd(List<Equipment> request) { return saveBatch(request); }
 
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
     public Boolean edit(Integer id, Equipment request) {
         if (id == null || id <= 0) throw new BusinessException(CodeEnum.PARAMS_ERROR);
         Equipment updateData = new Equipment();
         BeanUtils.copyProperties(request, updateData);
         updateData.setId(id);
         return updateById(updateData);
     }
 
     @Override
     public PageResult page(Integer pageNum, Integer pageSize, String name, Integer labId) {
         QueryWrapper<Equipment> qw = new QueryWrapper<>();
         qw.orderByDesc("create_time");
         if (CharSequenceUtil.isNotBlank(name)) qw.like("name", name);
         if (labId != null && labId > 0) qw.eq("lab_id", labId);
         Page<Equipment> page = equipmentMapper.selectPage(new Page<>(pageNum, pageSize), qw);
         PageResult<List<Equipment>> pr = new PageResult<>();
         pr.setRecords(page.getRecords());
         pr.setCurrent(page.getCurrent());
         pr.setTotal(page.getTotal());
         pr.setSize(page.getSize());
         return pr;
     }
 
     @Override
     public List<Equipment> list() {
         return equipmentMapper.selectList(new QueryWrapper<Equipment>().orderByDesc("create_time"));
     }
 }
