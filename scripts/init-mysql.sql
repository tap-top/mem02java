-- 创建数据库
CREATE DATABASE IF NOT EXISTS mem0 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE mem0;

-- 创建应用表
CREATE TABLE IF NOT EXISTS app (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    app_id VARCHAR(100) NOT NULL UNIQUE COMMENT '应用ID',
    name VARCHAR(255) NOT NULL COMMENT '应用名称',
    description TEXT COMMENT '应用描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应用表';

-- 创建智能体表
CREATE TABLE IF NOT EXISTS agent (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    agent_id VARCHAR(100) NOT NULL UNIQUE COMMENT '智能体ID',
    app_id BIGINT NOT NULL COMMENT '所属应用ID',
    name VARCHAR(255) NOT NULL COMMENT '智能体名称',
    description TEXT COMMENT '智能体描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_app_id (app_id),
    INDEX idx_agent_id (agent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='智能体表';

-- 创建记忆表
CREATE TABLE IF NOT EXISTS memory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    app_id BIGINT NOT NULL COMMENT '应用ID',
    agent_id BIGINT NOT NULL COMMENT '智能体ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    memory_id VARCHAR(100) NOT NULL UNIQUE COMMENT '记忆ID',
    memory_type VARCHAR(50) NOT NULL COMMENT '记忆类型',
    content TEXT NOT NULL COMMENT '记忆内容',
    metadata JSON COMMENT '元数据',
    embedding_id VARCHAR(100) COMMENT '嵌入向量ID',
    version INT DEFAULT 1 COMMENT '版本号',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_app_agent_user (app_id, agent_id, user_id),
    INDEX idx_memory_id (memory_id),
    INDEX idx_memory_type (memory_type),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='记忆表';

-- 创建提示词表
CREATE TABLE IF NOT EXISTS prompt (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    prompt_id VARCHAR(100) NOT NULL UNIQUE COMMENT '提示词ID',
    name VARCHAR(255) NOT NULL COMMENT '提示词名称',
    content TEXT NOT NULL COMMENT '提示词内容',
    description TEXT COMMENT '提示词描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_prompt_id (prompt_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='提示词表';

-- 插入默认数据
INSERT IGNORE INTO app (app_id, name, description) VALUES 
('1', '默认应用', '系统默认应用'),
('2', '测试应用', '用于测试的应用'),
('3', '示例应用', '示例应用');

INSERT IGNORE INTO agent (agent_id, app_id, name, description) VALUES 
('20001', 1, '默认智能体', '系统默认智能体'),
('3', 3, '示例智能体', '示例智能体');

INSERT IGNORE INTO prompt (prompt_id, name, content, description) VALUES 
('default', '默认提示词', '你是一个有用的AI助手。', '系统默认提示词'),
('memory', '记忆提示词', '基于以下记忆信息回答问题：{memory}', '记忆相关提示词'); 