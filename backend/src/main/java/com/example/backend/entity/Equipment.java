 package com.example.backend.entity;
 
 import com.baomidou.mybatisplus.annotation.*;
 import com.fasterxml.jackson.annotation.JsonFormat;
 import lombok.Data;
 import java.io.Serializable;
 import java.util.Date;
 
 @TableName(value = "equipment")
 @Data
 public class Equipment implements Serializable {
     @TableId(type = IdType.AUTO)
     private Integer id;
     private String name;
     private String model;
     private String code;
     private Integer labId;
     private Integer status;
     private String purchaseDate;
     private Integer maintenanceCycle;
     private String description;
     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
     private Date createTime;
     @TableField(value = "update_time", fill = FieldFill.UPDATE)
     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
     private Date updateTime;
     @TableField(exist = false)
     private static final long serialVersionUID = 1L;
 }
