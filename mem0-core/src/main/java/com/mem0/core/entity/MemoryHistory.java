package com.mem0.core.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 记忆历史实体类
 * 对应数据库表 memory_history
 * 
 * @author changyu496
 */
@Data
public class MemoryHistory {
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 记忆ID
     */
    private Long memoryId;
    
    /**
     * 历史内容
     */
    private String content;
    
    /**
     * 历史元数据（JSON格式）
     */
    private String metadata;
    
    /**
     * 版本号
     */
    private Integer version;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 设置默认值
     */
    public void setDefaults() {
        if (this.version == null) {
            this.version = 1;
        }
        if (this.metadata == null) {
            this.metadata = "{}";
        }
    }
} 