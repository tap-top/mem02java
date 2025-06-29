package com.mem0.sdk.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 记忆搜索结果
 * 
 * @author changyu496
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchMemoryResult {
    
    /**
     * 查询内容
     */
    private String query;
    
    /**
     * 搜索结果列表
     */
    private List<MemoryInfo> memories;
    
    /**
     * 总数量
     */
    private Long total;
    
    /**
     * 限制数量
     */
    private Integer limit;
    
    /**
     * 搜索耗时（毫秒）
     */
    private Long searchTime;
    
    /**
     * 是否包含向量搜索
     */
    private Boolean includeVectorSearch;
    
    /**
     * 相似度阈值
     */
    private Double similarityThreshold;
} 