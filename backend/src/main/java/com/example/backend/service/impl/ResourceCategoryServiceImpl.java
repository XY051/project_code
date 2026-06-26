package com.example.backend.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.common.enums.CodeEnum;
import com.example.backend.common.model.PageResult;
import com.example.backend.entity.ResourceCategory;
import com.example.backend.exception.BusinessException;
import com.example.backend.mapper.ResourceCategoryMapper;
import com.example.backend.service.IResourceCategoryService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceCategoryServiceImpl extends ServiceImpl<ResourceCategoryMapper, ResourceCategory>
        implements IResourceCategoryService {

    @Resource
    private ResourceCategoryMapper resourceCategoryMapper;

    @Override
    public Integer add(ResourceCategory request) {
        ResourceCategory saveData = new ResourceCategory();
        BeanUtils.copyProperties(request, saveData);
        resourceCategoryMapper.insert(saveData);
        return saveData.getId();
    }

    @Override
    public Boolean batchAdd(List<ResourceCategory> request) {
        return saveBatch(request);
    }

    @Override
    public Boolean del(Integer id) {
        return removeById(id);
    }

    @Override
    public Boolean batchDel(List<Integer> ids) {
        return removeByIds(ids);
    }

    @Override
    public Boolean edit(Integer id, ResourceCategory request) {
        ResourceCategory findData = resourceCategoryMapper.selectById(id);
        if (findData == null) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR, "资源分类不存在");
        }
        BeanUtils.copyProperties(request, findData);
        findData.setId(id);
        return updateById(findData);
    }

    @Override
    public PageResult<List<ResourceCategory>> queryPage(Integer pageNum, Integer pageSize, String name) {
        QueryWrapper<ResourceCategory> queryWrapper = new QueryWrapper<>();
        if (CharSequenceUtil.isNotBlank(name)) {
            queryWrapper.like("name", name);
        }
        queryWrapper.orderByDesc("id");
        Page<ResourceCategory> page = page(new Page<>(pageNum, pageSize), queryWrapper);
        PageResult<List<ResourceCategory>> pageResult = new PageResult<>();
        pageResult.setTotal(page.getTotal());
        pageResult.setSize(page.getSize());
        pageResult.setCurrent(page.getCurrent());
        pageResult.setRecords(page.getRecords());
        return pageResult;
    }

    @Override
    public List<ResourceCategory> getAll() {
        return list(new QueryWrapper<ResourceCategory>().orderByDesc("id"));
    }

    @Override
    public ResourceCategory getByIdDetail(Integer id) {
        return getById(id);
    }
}
