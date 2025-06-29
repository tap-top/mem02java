package com.mem0.sdk.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 记忆操作结果
 * 
 * @author changyu496
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoryOperationResult {
    
    /**
     * 记忆ID
     */
    private String id;
    
    /**
     * 记忆内容
     */
    private String memory;
    
    /**
     * 操作事件类型：ADD, UPDATE, DELETE, NONE
     */
    private String event;
    
    /**
     * 之前的记忆内容（用于UPDATE操作）
     */
    private String previousMemory;
    
    /**
     * 操作是否成功
     */
    @Builder.Default
    private Boolean success = true;
    
    /**
     * 错误信息
     */
    private String errorMessage;
} 