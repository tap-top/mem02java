package com.mem0.core.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 会话实体类
 * 对应数据库表 session
 * 
 * @author changyu496
 */
@Data
public class Session {
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 应用ID
     */
    private Long appId;
    
    /**
     * 智能体ID
     */
    private Long agentId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 会话状态：active-活跃，closed-关闭
     */
    private String status;
    
    /**
     * 会话元数据（JSON格式）
     */
    private String metadata;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 设置默认值
     */
    public void setDefaults() {
        if (this.sessionId == null) {
            this.sessionId = UUID.randomUUID().toString();
        }
        if (this.status == null) {
            this.status = "active";
        }
        if (this.metadata == null) {
            this.metadata = "{}";
        }
    }
} 