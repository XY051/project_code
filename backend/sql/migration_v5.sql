-- ============================================
-- Task 4: 用户与权限管理 + 教学过程监控
-- 数据库迁移脚本
-- ============================================

-- Phase 0.1: 改造 user 表（新增字段，保留原 role 字段兼容）
ALTER TABLE `user`
    ADD COLUMN IF NOT EXISTS `user_type` varchar(32) DEFAULT NULL COMMENT '用户类型(student/teacher/admin/social)',
    ADD COLUMN IF NOT EXISTS `institution` varchar(128) DEFAULT NULL COMMENT '所属院校';

-- Phase 0.2: 创建 role 表
CREATE TABLE IF NOT EXISTS `role` (
    `id` int NOT NULL AUTO_INCREMENT,
    `role_name` varchar(64) NOT NULL COMMENT '角色名称',
    `role_code` varchar(64) NOT NULL COMMENT '角色标识码',
    `description` varchar(255) DEFAULT NULL COMMENT '角色描述',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- Phase 0.3: 创建 permission 表
CREATE TABLE IF NOT EXISTS `permission` (
    `id` int NOT NULL AUTO_INCREMENT,
    `permission_name` varchar(64) NOT NULL COMMENT '权限名称',
    `permission_code` varchar(128) NOT NULL COMMENT '权限码(如user:add)',
    `description` varchar(255) DEFAULT NULL COMMENT '权限描述',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_permission_code` (`permission_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- Phase 0.4: 创建 user_role 关联表
CREATE TABLE IF NOT EXISTS `user_role` (
    `id` int NOT NULL AUTO_INCREMENT,
    `user_id` int NOT NULL COMMENT '用户ID',
    `role_id` int NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- Phase 0.5: 创建 role_permission 关联表
CREATE TABLE IF NOT EXISTS `role_permission` (
    `id` int NOT NULL AUTO_INCREMENT,
    `role_id` int NOT NULL COMMENT '角色ID',
    `permission_id` int NOT NULL COMMENT '权限ID',
    PRIMARY KEY (`id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- Phase 0.6: 创建 monitor_record 表
CREATE TABLE IF NOT EXISTS `monitor_record` (
    `id` int NOT NULL AUTO_INCREMENT,
    `user_id` int NOT NULL COMMENT '学生ID',
    `training_id` int DEFAULT NULL COMMENT '实训项目ID',
    `progress` decimal(5,2) DEFAULT 0.00 COMMENT '进度百分比',
    `duration` int DEFAULT 0 COMMENT '学习时长(分钟)',
    `status` tinyint DEFAULT 0 COMMENT '状态(0-未开始,1-进行中,2-已完成,3-异常)',
    `abnormal_info` varchar(500) DEFAULT NULL COMMENT '异常信息',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_training_id` (`training_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教学监控记录表';

-- Phase 0.7: 创建 teaching_guidance 表
CREATE TABLE IF NOT EXISTS `teaching_guidance` (
    `id` int NOT NULL AUTO_INCREMENT,
    `teacher_id` int NOT NULL COMMENT '教师ID',
    `student_id` int NOT NULL COMMENT '学生ID',
    `training_id` int DEFAULT NULL COMMENT '实训项目ID',
    `guidance_content` text COMMENT '指导内容',
    `guidance_time` datetime DEFAULT NULL COMMENT '指导时间',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_teacher_id` (`teacher_id`),
    KEY `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师指导记录表';

-- ============================================
-- 种子数据
-- ============================================

-- 角色种子数据
INSERT IGNORE INTO `role` (`id`, `role_name`, `role_code`, `description`, `create_time`, `update_time`) VALUES
(1, '管理员', 'admin', '系统管理员，拥有所有权限', NOW(), NOW()),
(2, '学生', 'student', '学生用户', NOW(), NOW()),
(3, '教师', 'teacher', '教师用户', NOW(), NOW()),
(4, '社会用户', 'social', '社会用户', NOW(), NOW());

-- 权限种子数据（基础 CRUD 权限码）
INSERT IGNORE INTO `permission` (`id`, `permission_name`, `permission_code`, `description`, `create_time`) VALUES
(1, '用户新增', 'user:add', '新增用户', NOW()),
(2, '用户编辑', 'user:edit', '编辑用户', NOW()),
(3, '用户删除', 'user:del', '删除用户', NOW()),
(4, '角色新增', 'role:add', '新增角色', NOW()),
(5, '角色编辑', 'role:edit', '编辑角色', NOW()),
(6, '角色删除', 'role:del', '删除角色', NOW()),
(7, '权限新增', 'permission:add', '新增权限', NOW()),
(8, '权限编辑', 'permission:edit', '编辑权限', NOW()),
(9, '权限删除', 'permission:del', '删除权限', NOW()),
(10, '用户查看', 'user:list', '查看用户列表', NOW()),
(11, '角色查看', 'role:list', '查看角色列表', NOW()),
(12, '权限查看', 'permission:list', '查看权限列表', NOW()),
(13, '监控新增', 'monitor:add', '新增监控记录', NOW()),
(14, '监控编辑', 'monitor:edit', '编辑监控记录', NOW()),
(15, '监控删除', 'monitor:del', '删除监控记录', NOW()),
(16, '监控查看', 'monitor:list', '查看监控记录', NOW()),
(17, '指导新增', 'guidance:add', '新增指导记录', NOW()),
(18, '指导编辑', 'guidance:edit', '编辑指导记录', NOW()),
(19, '指导删除', 'guidance:del', '删除指导记录', NOW()),
(20, '指导查看', 'guidance:list', '查看指导记录', NOW());

-- 给 admin 角色分配所有权限
INSERT IGNORE INTO `role_permission` (`id`, `role_id`, `permission_id`) VALUES
(1, 1, 1), (2, 1, 2), (3, 1, 3), (4, 1, 4), (5, 1, 5),
(6, 1, 6), (7, 1, 7), (8, 1, 8), (9, 1, 9), (10, 1, 10),
(11, 1, 11), (12, 1, 12), (13, 1, 13), (14, 1, 14), (15, 1, 15),
(16, 1, 16), (17, 1, 17), (18, 1, 18), (19, 1, 19), (20, 1, 20);

-- 给 teacher 角色分配监控和指导权限
INSERT IGNORE INTO `role_permission` (`id`, `role_id`, `permission_id`) VALUES
(21, 3, 13), (22, 3, 14), (23, 3, 16),
(24, 3, 17), (25, 3, 18), (26, 3, 20);

-- 给 student 角色分配查看权限
INSERT IGNORE INTO `role_permission` (`id`, `role_id`, `permission_id`) VALUES
(27, 2, 10), (28, 2, 16);

-- 将现有 admin 用户映射到 admin 角色（user.role='admin' → user_role.role_id=1）
INSERT IGNORE INTO `user_role` (`user_id`, `role_id`)
SELECT u.id, 1 FROM `user` u WHERE u.role = 'admin';

-- 将现有普通用户映射到 student 角色（user.role='user' → user_role.role_id=2）
INSERT IGNORE INTO `user_role` (`user_id`, `role_id`)
SELECT u.id, 2 FROM `user` u WHERE u.role = 'user';
