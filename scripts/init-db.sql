-- Mem0 Java 版本数据库初始化脚本
-- 数据库名: mem0
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_unicode_ci

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `mem0` 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE `mem0`;

-- 应用表
CREATE TABLE IF NOT EXISTS `app` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `app_key` varchar(64) NOT NULL COMMENT '应用密钥',
  `app_name` varchar(128) NOT NULL COMMENT '应用名称',
  `description` text COMMENT '应用描述',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1-启用，0-禁用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_app_key` (`app_key`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应用表';

-- 智能体表
CREATE TABLE IF NOT EXISTS `agent` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `app_id` bigint NOT NULL COMMENT '应用ID',
  `agent_id` varchar(64) NOT NULL COMMENT '智能体ID',
  `agent_name` varchar(128) NOT NULL COMMENT '智能体名称',
  `description` text COMMENT '智能体描述',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1-启用，0-禁用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_app_agent` (`app_id`, `agent_id`),
  KEY `idx_app_id` (`app_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='智能体表';

-- 记忆表
CREATE TABLE IF NOT EXISTS `memory` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `app_id` bigint NOT NULL COMMENT '应用ID',
  `agent_id` bigint NOT NULL COMMENT '智能体ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `memory_id` varchar(64) NOT NULL COMMENT '记忆ID',
  `memory_type` varchar(32) NOT NULL COMMENT '记忆类型：fact-事实记忆，procedural-程序记忆',
  `content` text NOT NULL COMMENT '记忆内容',
  `metadata` json COMMENT '元数据',
  `embedding_id` varchar(64) COMMENT '向量ID',
  `version` int NOT NULL DEFAULT '1' COMMENT '版本号',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_memory_id` (`memory_id`),
  KEY `idx_app_agent_user` (`app_id`, `agent_id`, `user_id`),
  KEY `idx_memory_type` (`memory_type`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='记忆表';

-- 记忆历史表
CREATE TABLE IF NOT EXISTS `memory_history` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `memory_id` bigint NOT NULL COMMENT '记忆ID',
  `content` text NOT NULL COMMENT '历史内容',
  `metadata` json COMMENT '历史元数据',
  `version` int NOT NULL COMMENT '版本号',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_memory_id` (`memory_id`),
  KEY `idx_version` (`version`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='记忆历史表';

-- 会话表
CREATE TABLE IF NOT EXISTS `session` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `app_id` bigint NOT NULL COMMENT '应用ID',
  `agent_id` bigint NOT NULL COMMENT '智能体ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `session_id` varchar(64) NOT NULL COMMENT '会话ID',
  `status` varchar(32) NOT NULL DEFAULT 'active' COMMENT '会话状态：active-活跃，closed-关闭',
  `metadata` json COMMENT '会话元数据',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_session_id` (`session_id`),
  KEY `idx_app_agent_user` (`app_id`, `agent_id`, `user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会话表';

-- 插入测试数据
INSERT INTO `app` (`app_key`, `app_name`, `description`, `status`) VALUES
('test_app_key', '测试应用', '这是一个测试应用', 1)
ON DUPLICATE KEY UPDATE `app_name` = VALUES(`app_name`);

INSERT INTO `agent` (`app_id`, `agent_id`, `agent_name`, `description`, `status`) VALUES
(1, 'test_agent_id', '测试智能体', '这是一个测试智能体', 1)
ON DUPLICATE KEY UPDATE `agent_name` = VALUES(`agent_name`); 