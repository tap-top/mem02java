package com.mem0.sdk.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 记忆信息
 * 
 * @author changyu496
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoryInfo {
    
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
     * 记忆类型
     */
    private String memoryType;
    
    /**
     * 记忆内容
     */
    private String content;
    
    /**
     * 元数据
     */
    private Map<String, Object> metadata;
    
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
     * 相似度分数（用于搜索结果）
     */
    private Double similarityScore;
} 