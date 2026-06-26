package com.example.backend.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.common.constants.LoginConstant;
import com.example.backend.common.enums.CodeEnum;
import com.example.backend.common.model.PageResult;
import com.example.backend.entity.Reservation;
import com.example.backend.exception.BusinessException;
import com.example.backend.mapper.ReservationMapper;
import com.example.backend.service.IReservationService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class ReservationServiceImpl extends ServiceImpl<ReservationMapper, Reservation>
        implements IReservationService {

    @Resource
    private ReservationMapper reservationMapper;

    @Override
    public Integer add(Reservation request) {
        Reservation saveData = new Reservation();
        BeanUtils.copyProperties(request, saveData);
        saveData.setReservationNo("RS-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        saveData.setStatus(CharSequenceUtil.isNotBlank(saveData.getStatus()) ? saveData.getStatus() : "待审核");
        trySetLoginUser(saveData);
        reservationMapper.insert(saveData);
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
    public Boolean edit(Integer id, Reservation request) {
        Reservation findData = reservationMapper.selectById(id);
        if (findData == null) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR, "预约不存在");
        }
        BeanUtils.copyProperties(request, findData);
        findData.setId(id);
        return updateById(findData);
    }

    @Override
    public Boolean audit(Integer id, String status, String auditComment) {
        Reservation reservation = reservationMapper.selectById(id);
        if (reservation == null) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR, "预约不存在");
        }
        reservation.setStatus(status);
        reservation.setAuditComment(auditComment);
        return updateById(reservation);
    }

    @Override
    public PageResult<List<Reservation>> queryPage(Integer pageNum, Integer pageSize, String targetName,
                                                   String reservationType, String status, Integer userId) {
        QueryWrapper<Reservation> queryWrapper = new QueryWrapper<>();
        if (CharSequenceUtil.isNotBlank(targetName)) {
            queryWrapper.like("target_name", targetName);
        }
        if (CharSequenceUtil.isNotBlank(reservationType)) {
            queryWrapper.eq("reservation_type", reservationType);
        }
        if (CharSequenceUtil.isNotBlank(status)) {
            queryWrapper.eq("status", status);
        }
        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }
        queryWrapper.orderByDesc("id");
        Page<Reservation> page = reservationMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
        PageResult<List<Reservation>> pageResult = new PageResult<>();
        pageResult.setTotal(page.getTotal());
        pageResult.setSize(page.getSize());
        pageResult.setCurrent(page.getCurrent());
        pageResult.setRecords(page.getRecords());
        return pageResult;
    }

    @Override
    public List<Reservation> getAll() {
        return list(new QueryWrapper<Reservation>().orderByDesc("id"));
    }

    private void trySetLoginUser(Reservation reservation) {
        try {
            Object userId = StpUtil.getSession().get(LoginConstant.USER_ID);
            Object userName = StpUtil.getSession().get(LoginConstant.REAL_NAME);
            if (userId instanceof Integer) {
                reservation.setUserId((Integer) userId);
            }
            if (userName instanceof String && !CharSequenceUtil.isNotBlank(reservation.getUserName())) {
                reservation.setUserName((String) userName);
            }
        } catch (Exception ignored) {
            // 兼容未登录演示场景
        }
    }
}
