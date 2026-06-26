package com.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.common.model.PageResult;
import com.example.backend.entity.ResourceCategory;

import java.util.List;

public interface IResourceCategoryService extends IService<ResourceCategory> {
    Integer add(ResourceCategory request);
    Boolean batchAdd(List<ResourceCategory> request);
    Boolean del(Integer id);
    Boolean batchDel(List<Integer> ids);
    Boolean edit(Integer id, ResourceCategory request);
    PageResult<List<ResourceCategory>> queryPage(Integer pageNum, Integer pageSize, String name);
    List<ResourceCategory> getAll();
    ResourceCategory getByIdDetail(Integer id);
}
