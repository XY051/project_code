package com.example.backend.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.common.constants.LoginConstant;
import com.example.backend.common.enums.CodeEnum;
import com.example.backend.common.model.PageResult;
import com.example.backend.entity.OpenApplication;
import com.example.backend.entity.TrainingResource;
import com.example.backend.exception.BusinessException;
import com.example.backend.mapper.OpenApplicationMapper;
import com.example.backend.mapper.TrainingResourceMapper;
import com.example.backend.service.IOpenApplicationService;
import com.example.backend.service.ITrainingResourceService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class OpenApplicationServiceImpl extends ServiceImpl<OpenApplicationMapper, OpenApplication>
        implements IOpenApplicationService {

    @Resource
    private OpenApplicationMapper openApplicationMapper;

    @Resource
    private TrainingResourceMapper trainingResourceMapper;

    @Resource
    private ITrainingResourceService trainingResourceService;

    @Override
    public Integer add(OpenApplication request) {
        if (request.getResourceId() == null) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR, "请选择开放资源");
        }
        TrainingResource resource = trainingResourceMapper.selectById(request.getResourceId());
        if (resource == null) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR, "资源不存在");
        }
        OpenApplication saveData = new OpenApplication();
        BeanUtils.copyProperties(request, saveData);
        saveData.setResourceName(resource.getName());
        saveData.setApplicationNo("OA-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        saveData.setAuditStatus(CharSequenceUtil.isNotBlank(saveData.getAuditStatus()) ? saveData.getAuditStatus() : "待审核");
        saveData.setUseStatus(CharSequenceUtil.isNotBlank(saveData.getUseStatus()) ? saveData.getUseStatus() : "待使用");
        trySetLoginUser(saveData);
        openApplicationMapper.insert(saveData);
        return saveData.getId();
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
    public Boolean edit(Integer id, OpenApplication request) {
        OpenApplication findData = openApplicationMapper.selectById(id);
        if (findData == null) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR, "申请不存在");
        }
        BeanUtils.copyProperties(request, findData);
        findData.setId(id);
        return updateById(findData);
    }

    @Override
    public Boolean audit(Integer id, String auditStatus, String auditComment) {
        OpenApplication application = openApplicationMapper.selectById(id);
        if (application == null) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR, "申请不存在");
        }
        application.setAuditStatus(auditStatus);
        application.setAuditComment(auditComment);
        if ("已通过".equals(auditStatus)) {
            if (!"使用中".equals(application.getUseStatus())) {
                Boolean reduced = trainingResourceService.reduceAvailableCount(application.getResourceId());
                if (!reduced) {
                    throw new BusinessException(CodeEnum.PARAMS_ERROR, "资源暂无可用数量");
                }
            }
            application.setUseStatus("使用中");
        } else if ("已驳回".equals(auditStatus) || "未通过".equals(auditStatus)) {
            application.setUseStatus("已驳回");
        }
        return updateById(application);
    }

    @Override
    public Boolean returnResource(Integer id) {
        OpenApplication application = openApplicationMapper.selectById(id);
        if (application == null) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR, "申请不存在");
        }
        if ("已归还".equals(application.getUseStatus())) {
            return true;
        }
        if ("使用中".equals(application.getUseStatus())) {
            trainingResourceService.increaseAvailableCount(application.getResourceId());
        }
        application.setUseStatus("已归还");
        return updateById(application);
    }

    @Override
    public PageResult<List<OpenApplication>> queryPage(Integer pageNum, Integer pageSize, String resourceName, Integer resourceId,
                                                       String auditStatus, String useStatus, Integer userId) {
        QueryWrapper<OpenApplication> queryWrapper = new QueryWrapper<>();
        if (CharSequenceUtil.isNotBlank(resourceName)) {
            queryWrapper.like("resource_name", resourceName);
        }
        if (resourceId != null) {
            queryWrapper.eq("resource_id", resourceId);
        }
        if (CharSequenceUtil.isNotBlank(auditStatus)) {
            queryWrapper.eq("audit_status", auditStatus);
        }
        if (CharSequenceUtil.isNotBlank(useStatus)) {
            queryWrapper.eq("use_status", useStatus);
        }
        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }
        queryWrapper.orderByDesc("id");
        Page<OpenApplication> page = openApplicationMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
        PageResult<List<OpenApplication>> pageResult = new PageResult<>();
        pageResult.setTotal(page.getTotal());
        pageResult.setSize(page.getSize());
        pageResult.setCurrent(page.getCurrent());
        pageResult.setRecords(page.getRecords());
        return pageResult;
    }

    @Override
    public List<OpenApplication> getAll() {
        return list(new QueryWrapper<OpenApplication>().orderByDesc("id"));
    }

    private void trySetLoginUser(OpenApplication application) {
        try {
            Object userId = StpUtil.getSession().get(LoginConstant.USER_ID);
            Object userName = StpUtil.getSession().get(LoginConstant.REAL_NAME);
            if (userId instanceof Integer) {
                application.setUserId((Integer) userId);
            }
            if (userName instanceof String && !CharSequenceUtil.isNotBlank(application.getUserName())) {
                application.setUserName((String) userName);
            }
        } catch (Exception ignored) {
            // 兼容未登录演示场景
        }
    }
}
