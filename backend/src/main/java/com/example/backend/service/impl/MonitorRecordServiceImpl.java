package com.example.backend.service.impl;

import com.example.backend.entity.MonitorRecord;
import com.example.backend.mapper.MonitorRecordMapper;
import com.example.backend.service.IMonitorRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class MonitorRecordServiceImpl extends ServiceImpl<MonitorRecordMapper, MonitorRecord>
        implements IMonitorRecordService {
}
