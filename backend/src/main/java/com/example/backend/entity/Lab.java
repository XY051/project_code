 package com.example.backend.entity;
 
 import com.baomidou.mybatisplus.annotation.*;
 import com.fasterxml.jackson.annotation.JsonFormat;
 import lombok.Data;
 import java.io.Serializable;
 import java.math.BigDecimal;
 import java.util.Date;
 
 @TableName(value = "lab")
 @Data
 public class Lab implements Serializable {
     @TableId(type = IdType.AUTO)
     private Integer id;
     private String name;
     private String location;
     private BigDecimal area;
     private Integer capacity;
     private String openTime;
     private String description;
     private Integer status;
     private String manager;
     private String managerPhone;
     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
     private Date createTime;
     @TableField(value = "update_time", fill = FieldFill.UPDATE)
     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
     private Date updateTime;
     @TableField(exist = false)
     private static final long serialVersionUID = 1L;
 }
