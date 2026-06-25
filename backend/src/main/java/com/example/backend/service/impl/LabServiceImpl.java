 package com.example.backend.service.impl;
 
 import cn.hutool.core.text.CharSequenceUtil;
 import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
 import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
 import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
 import com.example.backend.common.enums.CodeEnum;
 import com.example.backend.common.model.PageResult;
 import com.example.backend.entity.Lab;
 import com.example.backend.exception.BusinessException;
 import com.example.backend.mapper.LabMapper;
 import com.example.backend.service.ILabService;
 import jakarta.annotation.Resource;
 import org.springframework.beans.BeanUtils;
 import org.springframework.stereotype.Service;
 import java.util.List;
 
 @Service
 public class LabServiceImpl extends ServiceImpl<LabMapper, Lab> implements ILabService {
     @Resource
     private LabMapper labMapper;
 
     @Override
     public Integer add(Lab request) {
         Lab saveData = new Lab();
         BeanUtils.copyProperties(request, saveData);
         labMapper.insert(saveData);
         return saveData.getId();
     }
 
     @Override
     public Boolean batchAdd(List<Lab> request) {
         return saveBatch(request);
     }
 
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
     public Boolean edit(Integer id, Lab request) {
         if (id == null || id <= 0) throw new BusinessException(CodeEnum.PARAMS_ERROR);
         Lab updateData = new Lab();
         BeanUtils.copyProperties(request, updateData);
         updateData.setId(id);
         return updateById(updateData);
     }
 
     @Override
     public PageResult page(Integer pageNum, Integer pageSize, String name) {
         QueryWrapper<Lab> qw = new QueryWrapper<>();
         qw.orderByDesc("create_time");
         if (CharSequenceUtil.isNotBlank(name)) qw.like("name", name);
         Page<Lab> page = labMapper.selectPage(new Page<>(pageNum, pageSize), qw);
         PageResult<List<Lab>> pr = new PageResult<>();
         pr.setRecords(page.getRecords());
         pr.setCurrent(page.getCurrent());
         pr.setTotal(page.getTotal());
         pr.setSize(page.getSize());
         return pr;
     }
 
     @Override
     public List<Lab> list() {
         return labMapper.selectList(new QueryWrapper<Lab>().orderByDesc("create_time"));
     }
 }
