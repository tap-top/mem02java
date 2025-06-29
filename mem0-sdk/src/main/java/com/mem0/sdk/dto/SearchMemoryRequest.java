package com.mem0.sdk.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * 记忆搜索请求
 * 
 * @author changyu496
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchMemoryRequest {
    
    /**
     * 查询内容
     */
    private String query;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 智能体ID
     */
    private String agentId;
    
    /**
     * 应用ID
     */
    private String appId;
    
    /**
     * 限制数量
     */
    @Builder.Default
    private Integer limit = 10;
    
    /**
     * 过滤条件
     */
    private Map<String, Object> filters;
    
    /**
     * 记忆类型
     */
    private String memoryType;
    
    /**
     * 是否包含向量搜索
     */
    @Builder.Default
    private Boolean includeVectorSearch = true;
    
    /**
     * 相似度阈值
     */
    @Builder.Default
    private Double similarityThreshold = 0.7;
} 