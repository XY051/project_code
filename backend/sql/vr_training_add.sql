-- 请在 db_ich 数据库中执行
USE db_ich;

 -- =====================================================
 -- 第4人模块：实验室与场所管理 + 设施设备管理
 -- 新增表结构
 -- =====================================================
 
 DROP TABLE IF EXISTS `lab`;
 CREATE TABLE `lab` (
   `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
   `name` varchar(100) NOT NULL COMMENT '实验室名称',
   `location` varchar(255) DEFAULT NULL COMMENT '地点/位置',
   `area` decimal(10,2) DEFAULT NULL COMMENT '面积(平方米)',
   `capacity` int(11) NOT NULL DEFAULT 0 COMMENT '容量(人数)',
   `open_time` varchar(100) DEFAULT NULL COMMENT '开放时间',
   `description` text COMMENT '设备清单/描述',
   `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '使用状态(0-空闲,1-使用中,2-维护中)',
   `manager` varchar(50) DEFAULT NULL COMMENT '负责人',
   `manager_phone` varchar(20) DEFAULT NULL COMMENT '负责人电话',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   PRIMARY KEY (`id`) USING BTREE
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='实验室与场所表';
 
 DROP TABLE IF EXISTS `lab_booking`;
 CREATE TABLE `lab_booking` (
   `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
   `lab_id` int(20) NOT NULL COMMENT '实验室ID',
   `user_id` int(20) NOT NULL COMMENT '预约用户ID',
   `user_name` varchar(50) DEFAULT NULL COMMENT '预约人姓名',
   `purpose` varchar(255) DEFAULT NULL COMMENT '预约用途',
   `book_date` date NOT NULL COMMENT '预约日期',
   `start_time` time NOT NULL COMMENT '开始时间',
   `end_time` time NOT NULL COMMENT '结束时间',
   `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '状态(0-待审核,1-已通过,2-已拒绝,3-已取消)',
   `remark` varchar(500) DEFAULT NULL COMMENT '备注/审核意见',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   PRIMARY KEY (`id`) USING BTREE,
   KEY `idx_lab_id` (`lab_id`) USING BTREE,
   KEY `idx_user_id` (`user_id`) USING BTREE
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='实验室预约表';
 
 DROP TABLE IF EXISTS `equipment`;
 CREATE TABLE `equipment` (
   `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
   `name` varchar(100) NOT NULL COMMENT '设备名称',
   `model` varchar(100) DEFAULT NULL COMMENT '型号',
   `code` varchar(100) DEFAULT NULL COMMENT '设备编号',
   `lab_id` int(20) DEFAULT NULL COMMENT '所属实验室ID',
   `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '状态(0-正常,1-维修中,2-已报废)',
   `purchase_date` date DEFAULT NULL COMMENT '购置日期',
   `maintenance_cycle` int(11) DEFAULT NULL COMMENT '维护周期(天)',
   `description` text COMMENT '设备描述/备注',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   PRIMARY KEY (`id`) USING BTREE,
   KEY `idx_lab_id` (`lab_id`) USING BTREE
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='设施设备表';
 
 DROP TABLE IF EXISTS `maintenance_record`;
 CREATE TABLE `maintenance_record` (
   `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
   `equipment_id` int(20) NOT NULL COMMENT '设备ID',
   `maintenance_date` date NOT NULL COMMENT '维护日期',
   `maintenance_person` varchar(50) DEFAULT NULL COMMENT '维护人员',
   `type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '维护类型(0-定期维护,1-故障维修)',
   `content` text COMMENT '维护内容',
   `cost` decimal(10,2) DEFAULT NULL COMMENT '维护费用',
   `next_maintenance_date` date DEFAULT NULL COMMENT '下次维护日期',
   `remark` varchar(500) DEFAULT NULL COMMENT '备注',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   PRIMARY KEY (`id`) USING BTREE,
   KEY `idx_equipment_id` (`equipment_id`) USING BTREE
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='设备维护记录表';
