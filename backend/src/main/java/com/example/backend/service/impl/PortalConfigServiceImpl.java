package com.example.backend.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.common.enums.CodeEnum;
import com.example.backend.common.model.PageResult;
import com.example.backend.entity.PortalConfig;
import com.example.backend.exception.BusinessException;
import com.example.backend.mapper.PortalConfigMapper;
import com.example.backend.service.IPortalConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 门户配置表 服务实现类
 * </p>
 *
 * @author
 * @since 2026-06-26
 */
@Service
public class PortalConfigServiceImpl extends ServiceImpl<PortalConfigMapper, PortalConfig> implements IPortalConfigService {

    @Resource
    private PortalConfigMapper portalConfigMapper;

    @Override
    public Integer add(PortalConfig request) {
        PortalConfig saveData = new PortalConfig();
        BeanUtils.copyProperties(request, saveData);
        portalConfigMapper.insert(saveData);
        return saveData.getId();
    }

    @Override
    public Boolean del(Integer id) {
        if (id == null || id <= 0) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR);
        }
        return removeById(id);
    }

    @Override
    public Boolean edit(Integer id, PortalConfig request) {
        if (id == null || id <= 0) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR);
        }
        PortalConfig updateData = new PortalConfig();
        BeanUtils.copyProperties(request, updateData);
        updateData.setId(id);
        return updateById(updateData);
    }

    @Override
    public PageResult page(Integer pageNum, Integer pageSize, String configKey) {
        QueryWrapper<PortalConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        if (CharSequenceUtil.isNotBlank(configKey)) {
            queryWrapper.like("config_key", configKey);
        }
        Page<PortalConfig> page = portalConfigMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
        PageResult<List<PortalConfig>> pageResult = new PageResult<>();
        pageResult.setRecords(page.getRecords());
        pageResult.setCurrent(page.getCurrent());
        pageResult.setTotal(page.getTotal());
        pageResult.setSize(page.getSize());
        return pageResult;
    }

    @Override
    public List<PortalConfig> list() {
        QueryWrapper<PortalConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        return portalConfigMapper.selectList(queryWrapper);
    }

    @Override
    public Map<String, String> listAllConfigMap() {
        List<PortalConfig> configList = portalConfigMapper.selectList(null);
        Map<String, String> configMap = new HashMap<>();
        for (PortalConfig config : configList) {
            configMap.put(config.getConfigKey(), config.getConfigValue());
        }
        return configMap;
    }
}