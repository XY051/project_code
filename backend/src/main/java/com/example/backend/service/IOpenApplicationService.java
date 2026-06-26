package com.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.common.model.PageResult;
import com.example.backend.entity.OpenApplication;

import java.util.List;

public interface IOpenApplicationService extends IService<OpenApplication> {
    Integer add(OpenApplication request);
    Boolean del(Integer id);
    Boolean batchDel(List<Integer> ids);
    Boolean edit(Integer id, OpenApplication request);
    Boolean audit(Integer id, String auditStatus, String auditComment);
    Boolean returnResource(Integer id);
    PageResult<List<OpenApplication>> queryPage(Integer pageNum, Integer pageSize, String resourceName, Integer resourceId,
                                                String auditStatus, String useStatus, Integer userId);
    List<OpenApplication> getAll();
}
