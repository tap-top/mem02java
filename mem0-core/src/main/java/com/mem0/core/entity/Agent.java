package com.mem0.core.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 智能体实体类
 * 
 * @author changyu496
 */
@Data
public class Agent {
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
    private String agentId;
    
    /**
     * 智能体名称
     */
    private String agentName;
    
    /**
     * 智能体描述
     */
    private String description;
    
    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;
    
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
        if (this.status == null) {
            this.status = 1;
        }
    }
} 