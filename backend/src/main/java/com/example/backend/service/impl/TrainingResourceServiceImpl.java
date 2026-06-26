package com.example.backend.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.common.enums.CodeEnum;
import com.example.backend.common.model.PageResult;
import com.example.backend.entity.ResourceCategory;
import com.example.backend.entity.TrainingResource;
import com.example.backend.entity.vo.trainingresource.TrainingResourceVO;
import com.example.backend.exception.BusinessException;
import com.example.backend.mapper.ResourceCategoryMapper;
import com.example.backend.mapper.TrainingResourceMapper;
import com.example.backend.service.ITrainingResourceService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TrainingResourceServiceImpl extends ServiceImpl<TrainingResourceMapper, TrainingResource>
        implements ITrainingResourceService {

    @Resource
    private TrainingResourceMapper trainingResourceMapper;

    @Resource
    private ResourceCategoryMapper resourceCategoryMapper;

    @Override
    public Integer add(TrainingResource request) {
        if (request.getViewCount() == null) {
            request.setViewCount(0);
        }
        if (request.getAvailableCount() == null) {
            request.setAvailableCount(0);
        }
        if (!CharSequenceUtil.isNotBlank(request.getStatus())) {
            request.setStatus("可用");
        }
        trainingResourceMapper.insert(request);
        return request.getId();
    }

    @Override
    public Boolean batchAdd(List<TrainingResource> request) {
        return saveBatch(request);
    }

    @Override
    public Boolean del(Integer id) {
        return trainingResourceMapper.deleteById(id) > 0;
    }

    @Override
    public Boolean batchDel(List<Integer> ids) {
        return trainingResourceMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public Boolean edit(Integer id, TrainingResource request) {
        TrainingResource resource = trainingResourceMapper.selectById(id);
        if (resource == null) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR, "资源不存在");
        }
        request.setId(id);
        return trainingResourceMapper.updateById(request) > 0;
    }

    @Override
    public PageResult<List<TrainingResourceVO>> queryPage(Integer pageNum, Integer pageSize, String name,
                                                          Integer categoryId, String resourceType, String openType) {
        QueryWrapper<TrainingResource> queryWrapper = new QueryWrapper<>();
        if (CharSequenceUtil.isNotBlank(name)) {
            queryWrapper.and(wrapper -> wrapper.like("name", name).or().like("intro", name).or().like("description", name));
        }
        if (categoryId != null) {
            queryWrapper.eq("category_id", categoryId);
        }
        if (CharSequenceUtil.isNotBlank(resourceType)) {
            queryWrapper.eq("resource_type", resourceType);
        }
        if (CharSequenceUtil.isNotBlank(openType)) {
            queryWrapper.eq("open_type", openType);
        }
        queryWrapper.orderByDesc("create_time");
        Page<TrainingResource> page = trainingResourceMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
        PageResult<List<TrainingResourceVO>> pageResult = new PageResult<>();
        pageResult.setTotal(page.getTotal());
        pageResult.setSize(page.getSize());
        pageResult.setCurrent(page.getCurrent());
        pageResult.setRecords(convertVO(page.getRecords()));
        return pageResult;
    }

    @Override
    public List<TrainingResource> getAll() {
        return trainingResourceMapper.selectList(new QueryWrapper<TrainingResource>().orderByDesc("create_time"));
    }

    @Override
    public TrainingResource getByIdDetail(Integer id) {
        TrainingResource resource = trainingResourceMapper.selectById(id);
        if (resource != null) {
            resource.setViewCount((resource.getViewCount() == null ? 0 : resource.getViewCount()) + 1);
            trainingResourceMapper.updateById(resource);
        }
        return trainingResourceMapper.selectById(id);
    }

    @Override
    public PageResult<List<TrainingResourceVO>> getHotPage(Integer pageNum, Integer pageSize, String orderBy, String orderType) {
        QueryWrapper<TrainingResource> queryWrapper = new QueryWrapper<>();
        String column = CharSequenceUtil.isNotBlank(orderBy) ? orderBy : "view_count";
        if ("asc".equalsIgnoreCase(orderType)) {
            queryWrapper.orderByAsc(column);
        } else {
            queryWrapper.orderByDesc(column);
        }
        Page<TrainingResource> page = trainingResourceMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
        PageResult<List<TrainingResourceVO>> pageResult = new PageResult<>();
        pageResult.setTotal(page.getTotal());
        pageResult.setSize(page.getSize());
        pageResult.setCurrent(page.getCurrent());
        pageResult.setRecords(convertVO(page.getRecords()));
        return pageResult;
    }

    @Override
    public Boolean reduceAvailableCount(Integer id) {
        UpdateWrapper<TrainingResource> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id).gt("available_count", 0).setSql("available_count = available_count - 1");
        return update(wrapper);
    }

    @Override
    public Boolean increaseAvailableCount(Integer id) {
        UpdateWrapper<TrainingResource> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id).setSql("available_count = available_count + 1");
        return update(wrapper);
    }

    private List<TrainingResourceVO> convertVO(List<TrainingResource> resources) {
        List<TrainingResourceVO> list = new ArrayList<>();
        for (TrainingResource resource : resources) {
            TrainingResourceVO vo = new TrainingResourceVO();
            BeanUtils.copyProperties(resource, vo);
            if (resource.getCategoryId() != null) {
                ResourceCategory category = resourceCategoryMapper.selectById(resource.getCategoryId());
                if (category != null) {
                    vo.setCategoryName(category.getName());
                }
            }
            list.add(vo);
        }
        return list;
    }
}
