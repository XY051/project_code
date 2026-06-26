-- 第2人模块安全导入脚本：只补齐缺失表并写入 10 条带链接资源
USE db_ich;

CREATE TABLE IF NOT EXISTS `resource_category` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(100) NOT NULL COMMENT '分类名称',
  `description` varchar(500) DEFAULT NULL COMMENT '分类说明',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_resource_category_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='虚拟仿真资源分类表';

CREATE TABLE IF NOT EXISTS `training_resource` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '资源ID',
  `name` varchar(200) NOT NULL COMMENT '资源名称',
  `category_id` int DEFAULT NULL COMMENT '资源分类ID',
  `resource_type` varchar(50) DEFAULT '虚拟仿真' COMMENT '资源类型',
  `open_type` varchar(50) DEFAULT NULL COMMENT '开放类型',
  `resource_level` varchar(50) DEFAULT NULL COMMENT '资源等级',
  `cover_image` varchar(500) DEFAULT NULL COMMENT '封面图片',
  `intro` varchar(500) DEFAULT NULL COMMENT '资源简介',
  `description` text COMMENT '资源说明',
  `access_url` varchar(500) DEFAULT NULL COMMENT '实验入口链接',
  `available_count` int DEFAULT 0 COMMENT '可用数量',
  `status` varchar(30) DEFAULT '可用' COMMENT '资源状态',
  `view_count` int DEFAULT 0 COMMENT '浏览次数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_training_resource_name` (`name`),
  KEY `idx_training_resource_category` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='虚拟仿真资源表';

CREATE TABLE IF NOT EXISTS `open_application` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `application_no` varchar(50) NOT NULL COMMENT '申请编号',
  `resource_id` int NOT NULL COMMENT '资源ID',
  `resource_name` varchar(200) DEFAULT NULL COMMENT '资源名称',
  `user_id` int DEFAULT NULL COMMENT '申请用户ID',
  `user_name` varchar(100) DEFAULT NULL COMMENT '申请人',
  `applicant_org` varchar(200) DEFAULT NULL COMMENT '申请单位',
  `contact_phone` varchar(30) DEFAULT NULL COMMENT '联系电话',
  `open_type` varchar(50) DEFAULT NULL COMMENT '开放类型',
  `purpose` varchar(300) DEFAULT NULL COMMENT '使用目的',
  `use_date` date DEFAULT NULL COMMENT '使用日期',
  `participant_count` int DEFAULT 1 COMMENT '参与人数',
  `audit_status` varchar(30) DEFAULT '待审核' COMMENT '审核状态',
  `audit_comment` varchar(500) DEFAULT NULL COMMENT '审核意见',
  `use_status` varchar(30) DEFAULT '待使用' COMMENT '使用状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_open_application_no` (`application_no`),
  KEY `idx_open_application_resource` (`resource_id`),
  KEY `idx_open_application_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='共享开放申请表';

CREATE TABLE IF NOT EXISTS `reservation` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `reservation_no` varchar(50) NOT NULL COMMENT '预约编号',
  `reservation_type` varchar(50) DEFAULT NULL COMMENT '预约类型：资源/实训室/设备',
  `target_id` int DEFAULT NULL COMMENT '预约对象ID',
  `target_name` varchar(200) DEFAULT NULL COMMENT '预约对象名称',
  `user_id` int DEFAULT NULL COMMENT '预约用户ID',
  `user_name` varchar(100) DEFAULT NULL COMMENT '预约人',
  `applicant_org` varchar(200) DEFAULT NULL COMMENT '预约单位',
  `contact_phone` varchar(30) DEFAULT NULL COMMENT '联系电话',
  `purpose` varchar(300) DEFAULT NULL COMMENT '预约用途',
  `use_date` date DEFAULT NULL COMMENT '使用日期',
  `start_time` varchar(20) DEFAULT NULL COMMENT '开始时间',
  `end_time` varchar(20) DEFAULT NULL COMMENT '结束时间',
  `participant_count` int DEFAULT 1 COMMENT '参与人数',
  `status` varchar(30) DEFAULT '待审核' COMMENT '预约状态',
  `audit_comment` varchar(500) DEFAULT NULL COMMENT '审核意见',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_reservation_no` (`reservation_no`),
  KEY `idx_reservation_target` (`target_id`),
  KEY `idx_reservation_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='共享开放预约表';

CREATE TABLE IF NOT EXISTS `lab` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '实验室名称',
  `location` varchar(255) DEFAULT NULL COMMENT '地点/位置',
  `area` decimal(10,2) DEFAULT NULL COMMENT '面积(平方米)',
  `capacity` int NOT NULL DEFAULT 0 COMMENT '容量(人数)',
  `open_time` varchar(100) DEFAULT NULL COMMENT '开放时间',
  `description` text COMMENT '设备清单/描述',
  `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '使用状态(0-空闲,1-使用中,2-维护中)',
  `manager` varchar(50) DEFAULT NULL COMMENT '负责人',
  `manager_phone` varchar(20) DEFAULT NULL COMMENT '负责人电话',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实验室与场所表';

INSERT INTO `resource_category` (`name`, `description`) VALUES
('交通运输', '智能网联汽车、车路协同等虚拟仿真实训资源'),
('财经商贸', '税务申报、数电发票、金融科技等虚拟仿真实训资源'),
('电子信息', '智能语音交互、信息系统等虚拟仿真实训资源'),
('文化艺术', '数字文化、工业记忆、场景设计等虚拟仿真实训资源'),
('新闻传播', '直播电商、内容运营等虚拟仿真实训资源')
ON DUPLICATE KEY UPDATE
  `description` = VALUES(`description`),
  `update_time` = CURRENT_TIMESTAMP;

INSERT INTO `training_resource`
(`name`, `category_id`, `resource_type`, `open_type`, `resource_level`, `cover_image`, `intro`, `description`, `access_url`, `available_count`, `status`, `view_count`)
VALUES
('智能网联测试技术虚拟仿真', (SELECT id FROM resource_category WHERE name = '交通运输'), '虚拟仿真', '校内实训', '在线仿真资源', '/file/files/vr-cover-01-smart-vehicle-test.png', '面向智能网联测试技术的虚拟仿真实训资源。', '支持智能网联汽车测试流程、场景识别与实验操作训练。', 'https://xnfz.cqtbi.edu.cn/IntelligentVehicleTechnologyV1.5/index.html', 18, '可用', 3021),
('山城办税之旅·税务申报虚拟仿真教学资源建设', (SELECT id FROM resource_category WHERE name = '财经商贸'), '虚拟仿真', '跨专业共享', '在线仿真资源', '/file/files/vr-cover-02-tax-filing.png', '围绕税务申报业务流程开展虚拟仿真实训。', '适合财经商贸类课程开展社会开放和跨专业共享。', 'https://xnfz.cqtbi.edu.cn/shuiWuShenBao/index.html', 18, '可用', 3315),
('实事求是·数电发票管理实战虚拟仿真教学资源建设', (SELECT id FROM resource_category WHERE name = '财经商贸'), '虚拟仿真', '跨专业共享', '在线仿真资源', '/file/files/vr-cover-03-e-invoice.png', '围绕数电发票管理实战开展虚拟仿真实训。', '覆盖数电发票业务办理、风险识别和管理流程。', 'https://xnfz.cqtbi.edu.cn/shuDianFaPiao/index.html', 18, '可用', 1868),
('金融科技产品实战应用虚拟仿真实训项目', (SELECT id FROM resource_category WHERE name = '财经商贸'), '虚拟仿真', '社会开放', '在线仿真资源', '/file/files/vr-cover-04-fintech.png', '面向金融科技产品应用场景开展实战化虚拟仿真实训。', '支持金融科技产品体验、流程训练与应用分析。', 'https://xnfz.cqtbi.edu.cn/finTech/index.html', 18, '可用', 1254),
('智能网联车路云协同系统', (SELECT id FROM resource_category WHERE name = '电子信息'), '虚拟仿真', '跨院校共享', '在线仿真资源', '/file/files/vr-cover-05-v2x-cloud.png', '面向智能网联车路云协同系统开展线上实训。', '适合电子信息、汽车类专业交叉实训。', 'https://xnfz.cqtbi.edu.cn/IntroductionOfCar2_v1.8/index.html', 18, '可用', 2949),
('重庆工业记忆·影视动画虚拟仿真之旅', (SELECT id FROM resource_category WHERE name = '文化艺术'), '虚拟仿真', '社会开放', '在线仿真资源', '/file/files/vr-cover-06-industrial-animation.png', '围绕工业空间和影视动画场景开展虚拟仿真实训。', '用于文化艺术和数字媒体方向场景化实践。', 'https://xnfz.cqtbi.edu.cn/industrialSpace/H5/HTML5/CQIndustry002-HTML5-Shipping.html', 18, '可用', 1360),
('倾斜摄影模型数据', (SELECT id FROM resource_category WHERE name = '文化艺术'), '虚拟仿真', '校内实训', '在线仿真资源', '/file/files/vr-cover-07-photogrammetry.png', '面向倾斜摄影模型数据认知与应用的虚拟仿真实训。', '支持三维模型观察、数据应用与空间认知训练。', 'https://xnfz.cqtbi.edu.cn/obliquePhotography/H5/HTML5/QXSY001-HTML5-Shipping.html', 18, '可用', 1764),
('大型助农直播虚拟仿真', (SELECT id FROM resource_category WHERE name = '新闻传播'), '虚拟仿真', '社会开放', '在线仿真资源', '/file/files/vr-cover-08-agri-livestream.png', '围绕助农直播业务流程开展沉浸式虚拟仿真实训。', '覆盖直播策划、直播间搭建和运营复盘。', 'https://xnfz.cqtbi.edu.cn/liveBroadcast/H5/HTML5/ZhiBoJiCheng001-HTML5-Shipping.html', 18, '可用', 2635),
('传统文化数字化场景设计虚拟仿真资源', (SELECT id FROM resource_category WHERE name = '文化艺术'), '虚拟仿真', '跨专业共享', '在线仿真资源', '/file/files/vr-cover-09-cultural-scene.png', '面向传统文化数字化场景设计开展虚拟仿真实训。', '支持数字文化场景设计、交互体验和展示训练。', 'https://xnfz.cqtbi.edu.cn/traditionalCulture/H5/HTML5/WenHuaShiKu001-HTML5-Shipping.html', 18, '可用', 2289),
('智能网联汽车语音交互系统', (SELECT id FROM resource_category WHERE name = '电子信息'), '虚拟仿真', '跨院校共享', '在线仿真资源', '/file/files/vr-cover-10-voice-interaction.png', '面向智能网联汽车语音交互系统开展虚拟仿真实训。', '支持语音交互系统流程认知与应用训练。', 'https://xnfz.cqtbi.edu.cn/IntelligentVoiceSystem/CarFour_V0.1.1/index.html', 18, '可用', 2402)
ON DUPLICATE KEY UPDATE
  `category_id` = VALUES(`category_id`),
  `resource_type` = VALUES(`resource_type`),
  `open_type` = VALUES(`open_type`),
  `resource_level` = VALUES(`resource_level`),
  `cover_image` = VALUES(`cover_image`),
  `intro` = VALUES(`intro`),
  `description` = VALUES(`description`),
  `access_url` = VALUES(`access_url`),
  `available_count` = VALUES(`available_count`),
  `status` = VALUES(`status`),
  `view_count` = VALUES(`view_count`),
  `update_time` = CURRENT_TIMESTAMP;

INSERT INTO `lab` (`name`, `location`, `area`, `capacity`, `open_time`, `description`, `status`, `manager`, `manager_phone`)
SELECT '智能网联汽车虚拟仿真实训室', '实训楼 A305', 120.00, 48, '周一至周五 08:30-17:30', '面向智能网联汽车、车路协同和交通运输类课程开放。', 0, '刘老师', '023-60000001'
WHERE NOT EXISTS (SELECT 1 FROM `lab` WHERE `name` = '智能网联汽车虚拟仿真实训室');

INSERT INTO `lab` (`name`, `location`, `area`, `capacity`, `open_time`, `description`, `status`, `manager`, `manager_phone`)
SELECT '数字财经虚拟仿真实训室', '实训楼 B206', 96.00, 40, '周一至周五 09:00-18:00', '支持税务申报、数电发票和金融科技产品应用等共享开放实训。', 0, '陈老师', '023-60000002'
WHERE NOT EXISTS (SELECT 1 FROM `lab` WHERE `name` = '数字财经虚拟仿真实训室');

INSERT INTO `lab` (`name`, `location`, `area`, `capacity`, `open_time`, `description`, `status`, `manager`, `manager_phone`)
SELECT '数字文化内容创作实训室', '创意实训中心 C102', 135.00, 36, '周二至周六 09:00-17:30', '用于直播运营、文化数字化场景设计、影视动画等开放实训。', 0, '张老师', '023-60000003'
WHERE NOT EXISTS (SELECT 1 FROM `lab` WHERE `name` = '数字文化内容创作实训室');
