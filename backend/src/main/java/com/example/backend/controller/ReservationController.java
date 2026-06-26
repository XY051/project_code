package com.example.backend.controller;

import com.example.backend.common.model.PageResult;
import com.example.backend.common.result.BaseResponse;
import com.example.backend.common.result.Result;
import com.example.backend.entity.Reservation;
import com.example.backend.service.IReservationService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservation")
public class ReservationController {

    @Resource
    private IReservationService reservationService;

    @PostMapping("/add")
    public BaseResponse<Integer> add(@RequestBody Reservation request) {
        return Result.success(reservationService.add(request));
    }

    @GetMapping("/del")
    public BaseResponse<Boolean> del(@RequestParam Integer id) {
        return Result.success(reservationService.del(id));
    }

    @PostMapping("/batchDel")
    public BaseResponse<Boolean> batchDel(@RequestBody List<Integer> ids) {
        return Result.success(reservationService.batchDel(ids));
    }

    @PostMapping("/edit")
    public BaseResponse<Boolean> edit(@RequestParam Integer id, @RequestBody Reservation request) {
        return Result.success(reservationService.edit(id, request));
    }

    @PostMapping("/audit")
    public BaseResponse<Boolean> audit(@RequestParam Integer id,
                                       @RequestParam String status,
                                       @RequestParam(required = false) String auditComment) {
        return Result.success(reservationService.audit(id, status, auditComment));
    }

    @GetMapping("/page")
    public BaseResponse<PageResult<List<Reservation>>> page(@RequestParam Integer pageNum,
                                                           @RequestParam Integer pageSize,
                                                           @RequestParam(required = false) String targetName,
                                                           @RequestParam(required = false) String reservationType,
                                                           @RequestParam(required = false) String status,
                                                           @RequestParam(required = false) Integer userId) {
        return Result.success(reservationService.queryPage(pageNum, pageSize, targetName, reservationType, status, userId));
    }

    @GetMapping("/list")
    public BaseResponse<List<Reservation>> list() {
        return Result.success(reservationService.getAll());
    }
}
