package com.example.backend.service.impl;

import com.example.backend.entity.TeachingGuidance;
import com.example.backend.mapper.TeachingGuidanceMapper;
import com.example.backend.service.ITeachingGuidanceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class TeachingGuidanceServiceImpl extends ServiceImpl<TeachingGuidanceMapper, TeachingGuidance>
        implements ITeachingGuidanceService {
}
