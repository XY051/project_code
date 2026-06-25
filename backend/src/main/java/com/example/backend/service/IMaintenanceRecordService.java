package com.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.common.model.PageResult;
import com.example.backend.entity.MaintenanceRecord;
import java.util.List;

public interface IMaintenanceRecordService extends IService<MaintenanceRecord> {
    Integer add(MaintenanceRecord request);
    Boolean batchAdd(List<MaintenanceRecord> request);
    Boolean del(Integer id);
    Boolean batchDel(List<Integer> ids);
    Boolean edit(Integer id, MaintenanceRecord request);
    PageResult page(Integer pageNum, Integer pageSize, Integer equipmentId);
    List<MaintenanceRecord> list();
}
