package com.example.backend.entity.vo.trainingresource;

import com.baomidou.mybatisplus.annotation.TableField;
import com.example.backend.entity.TrainingResource;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TrainingResourceVO extends TrainingResource {

    @TableField(exist = false)
    private String categoryName;
}
