-- ================================================
-- 虚拟仿真实训平台 - 第1人数据库迁移脚本
-- 模块：平台门户首页 + 新闻公告 + 综合管理与效能分析
-- ================================================

-- 1. 改造 banner 表：新增排序、链接类型、链接地址、描述字段
ALTER TABLE `banner`
    ADD COLUMN `sort_order` INT NULL DEFAULT 0 COMMENT '排序字段（值越小越靠前）' AFTER `update_time`,
    ADD COLUMN `link_type` VARCHAR(20) NULL DEFAULT 'none' COMMENT '链接类型：url-外部链接, page-内部页面, none-无链接' AFTER `sort_order`,
    ADD COLUMN `link_url` VARCHAR(500) NULL DEFAULT NULL COMMENT '链接地址' AFTER `link_type`,
    ADD COLUMN `description` VARCHAR(500) NULL DEFAULT NULL COMMENT '描述文案' AFTER `link_url`;

-- 2. 新增 portal_config 表：门户配置表
CREATE TABLE IF NOT EXISTS `portal_config` (
    `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
    `config_value` TEXT NULL COMMENT '配置值',
    `config_type` VARCHAR(20) NULL DEFAULT 'text' COMMENT '配置类型：text-文本, image-图片, switch-开关, json-JSON数据',
    `remark` VARCHAR(255) NULL COMMENT '配置说明',
    `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门户配置表';

-- 插入默认门户配置
INSERT INTO `portal_config` (`config_key`, `config_value`, `config_type`, `remark`) VALUES
    ('platform_name', '虚拟仿真实训平台', 'text', '平台名称'),
    ('platform_description', '构建虚拟仿真实训教学体系，推动实践教学数字化转型', 'text', '平台描述'),
    ('show_quick_entry', 'true', 'switch', '是否显示快捷入口'),
    ('show_overview', 'true', 'switch', '是否显示数据概览'),
    ('show_hot_resources', 'true', 'switch', '是否显示热门资源'),
    ('show_hot_trainings', 'true', 'switch', '是否显示热门实训项目'),
    ('show_hot_news', 'true', 'switch', '是否显示最新公告');

-- 3. 新增 efficiency_report 表：效能分析报表表
CREATE TABLE IF NOT EXISTS `efficiency_report` (
    `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `report_name` VARCHAR(200) NOT NULL COMMENT '报表名称',
    `report_type` VARCHAR(50) NOT NULL COMMENT '报表类型：resource-资源利用, training-实训效能, user-用户活跃, comprehensive-综合分析',
    `data_json` TEXT NULL COMMENT '报表数据JSON',
    `chart_type` VARCHAR(50) NULL DEFAULT 'bar' COMMENT '图表类型：bar-柱状图, line-折线图, pie-饼图, radar-雷达图',
    `create_user_id` INT NULL COMMENT '创建用户ID',
    `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='效能分析报表表';