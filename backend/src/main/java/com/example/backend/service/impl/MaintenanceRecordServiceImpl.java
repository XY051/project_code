package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.common.enums.CodeEnum;
import com.example.backend.common.model.PageResult;
import com.example.backend.entity.MaintenanceRecord;
import com.example.backend.exception.BusinessException;
import com.example.backend.mapper.MaintenanceRecordMapper;
import com.example.backend.service.IMaintenanceRecordService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MaintenanceRecordServiceImpl extends ServiceImpl<MaintenanceRecordMapper, MaintenanceRecord> implements IMaintenanceRecordService {
    @Resource
    private MaintenanceRecordMapper maintenanceRecordMapper;

    @Override
    public Integer add(MaintenanceRecord request) {
        MaintenanceRecord saveData = new MaintenanceRecord();
        BeanUtils.copyProperties(request, saveData);
        maintenanceRecordMapper.insert(saveData);
        return saveData.getId();
    }

    @Override
    public Boolean batchAdd(List<MaintenanceRecord> request) { return saveBatch(request); }

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
    public Boolean edit(Integer id, MaintenanceRecord request) {
        if (id == null || id <= 0) throw new BusinessException(CodeEnum.PARAMS_ERROR);
        MaintenanceRecord updateData = new MaintenanceRecord();
        BeanUtils.copyProperties(request, updateData);
        updateData.setId(id);
        return updateById(updateData);
    }

    @Override
    public PageResult page(Integer pageNum, Integer pageSize, Integer equipmentId) {
        QueryWrapper<MaintenanceRecord> qw = new QueryWrapper<>();
        qw.orderByDesc("create_time");
        if (equipmentId != null && equipmentId > 0) qw.eq("equipment_id", equipmentId);
        Page<MaintenanceRecord> page = maintenanceRecordMapper.selectPage(new Page<>(pageNum, pageSize), qw);
        PageResult<List<MaintenanceRecord>> pr = new PageResult<>();
        pr.setRecords(page.getRecords());
        pr.setCurrent(page.getCurrent());
        pr.setTotal(page.getTotal());
        pr.setSize(page.getSize());
        return pr;
    }

    @Override
    public List<MaintenanceRecord> list() {
        return maintenanceRecordMapper.selectList(new QueryWrapper<MaintenanceRecord>().orderByDesc("create_time"));
    }
}
