package com.mem0.core.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 记忆实体类
 * 对应数据库表 memory
 * 
 * @author changyu496
 */
@Data
public class Memory {
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
     * 记忆ID
     */
    private String memoryId;
    
    /**
     * 记忆类型：fact-事实记忆，procedural-程序记忆
     */
    private String memoryType;
    
    /**
     * 记忆内容
     */
    private String content;
    
    /**
     * 元数据（JSON格式）
     */
    private String metadata;
    
    /**
     * 向量ID
     */
    private String embeddingId;
    
    /**
     * 版本号
     */
    private Integer version;
    
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
        if (this.memoryId == null) {
            this.memoryId = UUID.randomUUID().toString();
        }
        if (this.memoryType == null) {
            this.memoryType = "fact";
        }
        if (this.version == null) {
            this.version = 1;
        }
        if (this.metadata == null) {
            this.metadata = "{}";
        }
    }
} 