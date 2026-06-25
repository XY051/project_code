 package com.example.backend.entity;
 
 import com.baomidou.mybatisplus.annotation.*;
 import com.fasterxml.jackson.annotation.JsonFormat;
 import lombok.Data;
 import java.io.Serializable;
 import java.math.BigDecimal;
 import java.util.Date;
 
 @TableName(value = "maintenance_record")
 @Data
 public class MaintenanceRecord implements Serializable {
     @TableId(type = IdType.AUTO)
     private Integer id;
     private Integer equipmentId;
     private String maintenanceDate;
     private String maintenancePerson;
     private Integer type;
     private String content;
     private BigDecimal cost;
     private String nextMaintenanceDate;
     private String remark;
     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
     private Date createTime;
     @TableField(value = "update_time", fill = FieldFill.UPDATE)
     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
     private Date updateTime;
     @TableField(exist = false)
     private static final long serialVersionUID = 1L;
 }
