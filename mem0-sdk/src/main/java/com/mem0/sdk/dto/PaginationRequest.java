package com.mem0.sdk.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * 分页查询请求
 * 
 * @author changyu496
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginationRequest {
    
    /**
     * 页码（从1开始）
     */
    @Builder.Default
    private Integer page = 1;
    
    /**
     * 每页大小
     */
    @Builder.Default
    private Integer size = 20;
    
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
     * 记忆类型
     */
    private String memoryType;
    
    /**
     * 过滤条件
     */
    private Map<String, Object> filters;
    
    /**
     * 排序字段
     */
    private String sortBy;
    
    /**
     * 排序方向：ASC, DESC
     */
    @Builder.Default
    private String sortDirection = "DESC";
    
    /**
     * 获取偏移量
     * 
     * @return 偏移量
     */
    public int getOffset() {
        return (page - 1) * size;
    }
    
    /**
     * 获取限制数量
     * 
     * @return 限制数量
     */
    public int getLimit() {
        return size;
    }
} 