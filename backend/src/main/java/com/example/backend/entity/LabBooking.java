 package com.example.backend.entity;
 
 import com.baomidou.mybatisplus.annotation.*;
 import com.fasterxml.jackson.annotation.JsonFormat;
 import lombok.Data;
 import java.io.Serializable;
 import java.util.Date;
 
 @TableName(value = "lab_booking")
 @Data
 public class LabBooking implements Serializable {
     @TableId(type = IdType.AUTO)
     private Integer id;
     private Integer labId;
     private Integer userId;
     private String userName;
     private String purpose;
     private String bookDate;
     private String startTime;
     private String endTime;
     private Integer status;
     private String remark;
     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
     private Date createTime;
     @TableField(value = "update_time", fill = FieldFill.UPDATE)
     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
     private Date updateTime;
     @TableField(exist = false)
     private static final long serialVersionUID = 1L;
 }
