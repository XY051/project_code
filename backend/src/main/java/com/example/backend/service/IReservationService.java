package com.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.common.model.PageResult;
import com.example.backend.entity.Reservation;

import java.util.List;

public interface IReservationService extends IService<Reservation> {
    Integer add(Reservation request);
    Boolean del(Integer id);
    Boolean batchDel(List<Integer> ids);
    Boolean edit(Integer id, Reservation request);
    Boolean audit(Integer id, String status, String auditComment);
    PageResult<List<Reservation>> queryPage(Integer pageNum, Integer pageSize, String targetName,
                                            String reservationType, String status, Integer userId);
    List<Reservation> getAll();
}
