package com.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.common.model.PageResult;
import com.example.backend.entity.PortalConfig;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 门户配置表 服务类
 * </p>
 *
 * @author
 * @since 2026-06-26
 */
public interface IPortalConfigService extends IService<PortalConfig> {

    /**
     * 新增
     */
    Integer add(PortalConfig request);

    /**
     * 删除
     */
    Boolean del(Integer id);

    /**
     * 编辑
     */
    Boolean edit(Integer id, PortalConfig request);

    /**
     * 分页查询
     */
    PageResult page(Integer pageNum, Integer pageSize, String configKey);

    /**
     * 查询所有配置
     */
    List<PortalConfig> list();

    /**
     * 获取所有配置的Key-Value映射（供前端门户使用）
     */
    Map<String, String> listAllConfigMap();
}