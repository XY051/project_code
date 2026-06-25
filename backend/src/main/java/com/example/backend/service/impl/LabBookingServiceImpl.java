 package com.example.backend.service.impl;
 
 import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
 import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
 import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.common.enums.CodeEnum;
import com.example.backend.common.model.PageResult;
import com.example.backend.entity.Lab;
import com.example.backend.entity.LabBooking;
import com.example.backend.exception.BusinessException;
import com.example.backend.mapper.LabBookingMapper;
import com.example.backend.service.ILabBookingService;
import com.example.backend.service.ILabService;
import jakarta.annotation.Resource;
 import org.springframework.beans.BeanUtils;
 import org.springframework.stereotype.Service;
 import java.util.List;
 
 @Service
public class LabBookingServiceImpl extends ServiceImpl<LabBookingMapper, LabBooking> implements ILabBookingService {
    @Resource
    private LabBookingMapper labBookingMapper;
    @Resource
    private ILabService labService;
 
     @Override
     public Integer add(LabBooking request) {
         LabBooking saveData = new LabBooking();
         BeanUtils.copyProperties(request, saveData);
         labBookingMapper.insert(saveData);
         return saveData.getId();
     }
 
     @Override
     public Boolean batchAdd(List<LabBooking> request) { return saveBatch(request); }
 
     @Override
     public Boolean del(Integer id) {
         if (id == null || id <= 0) throw new BusinessException(CodeEnum.PARAMS_ERROR);
         return removeById(id);
     }
 
     @Override
     public Boolean batchDel(List<Integer> ids) {
         if (ids == null || ids.isEmpty()) throw new BusinessException(CodeEnum.PARAMS_ERROR);
         return removeByIds(ids);
     }
 
    @Override
    public Boolean edit(Integer id, LabBooking request) {
        if (id == null || id <= 0) throw new BusinessException(CodeEnum.PARAMS_ERROR);

       // 获取原始预约记录（需要 labId）
       LabBooking original = labBookingMapper.selectById(id);
       if (original == null) throw new BusinessException(CodeEnum.NULL_ERROR);

        LabBooking updateData = new LabBooking();
        BeanUtils.copyProperties(request, updateData);
        updateData.setId(id);
        boolean result = updateById(updateData);

        // 根据预约状态同步更新实验室状态
        Integer newStatus = request.getStatus();
        if (newStatus != null) {
            if (newStatus == 1) {
                // 审核通过 → 实验室设为"使用中"
                Lab labUpdate = new Lab();
                labUpdate.setId(original.getLabId());
                labUpdate.setStatus(1);
                labService.updateById(labUpdate);
            } else if (newStatus == 2 || newStatus == 3) {
                // 拒绝或取消 → 检查该实验室是否还有其他通过的预约
                QueryWrapper<LabBooking> qw = new QueryWrapper<>();
                qw.eq("lab_id", original.getLabId());
                qw.eq("status", 1);
                qw.ne("id", id);
                Long activeCount = labBookingMapper.selectCount(qw);
                if (activeCount == 0) {
                    // 没有其他通过预约 → 实验室恢复"空闲"
                    Lab labUpdate = new Lab();
                    labUpdate.setId(original.getLabId());
                    labUpdate.setStatus(0);
                    labService.updateById(labUpdate);
                }
            }
        }

        return result;
    }
 
     @Override
     public PageResult page(Integer pageNum, Integer pageSize, Integer labId, Integer userId) {
         QueryWrapper<LabBooking> qw = new QueryWrapper<>();
         qw.orderByDesc("create_time");
         if (labId != null && labId > 0) qw.eq("lab_id", labId);
         if (userId != null && userId > 0) qw.eq("user_id", userId);
         Page<LabBooking> page = labBookingMapper.selectPage(new Page<>(pageNum, pageSize), qw);
         PageResult<List<LabBooking>> pr = new PageResult<>();
         pr.setRecords(page.getRecords());
         pr.setCurrent(page.getCurrent());
         pr.setTotal(page.getTotal());
         pr.setSize(page.getSize());
         return pr;
     }
 
     @Override
     public List<LabBooking> list() {
         return labBookingMapper.selectList(new QueryWrapper<LabBooking>().orderByDesc("create_time"));
     }
 }
