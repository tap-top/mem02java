package com.mem0.sdk.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * 记忆更新请求
 * 
 * @author changyu496
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMemoryRequest {
    
    /**
     * 记忆内容
     */
    private String content;
    
    /**
     * 元数据
     */
    private Map<String, Object> metadata;
    
    /**
     * 记忆类型
     */
    private String memoryType;
    
    /**
     * 版本号
     */
    private Integer version;
} 