package com.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.common.model.PageResult;
import com.example.backend.entity.TrainingResource;
import com.example.backend.entity.vo.trainingresource.TrainingResourceVO;

import java.util.List;

public interface ITrainingResourceService extends IService<TrainingResource> {
    Integer add(TrainingResource request);
    Boolean batchAdd(List<TrainingResource> request);
    Boolean del(Integer id);
    Boolean batchDel(List<Integer> ids);
    Boolean edit(Integer id, TrainingResource request);
    PageResult<List<TrainingResourceVO>> queryPage(Integer pageNum, Integer pageSize, String name,
                                                   Integer categoryId, String resourceType, String openType);
    List<TrainingResource> getAll();
    TrainingResource getByIdDetail(Integer id);
    PageResult<List<TrainingResourceVO>> getHotPage(Integer pageNum, Integer pageSize, String orderBy, String orderType);
    Boolean reduceAvailableCount(Integer id);
    Boolean increaseAvailableCount(Integer id);
}
