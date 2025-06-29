package com.mem0.sdk.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 记忆添加请求
 * 
 * @author changyu496
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddMemoryRequest {
    
    /**
     * 消息列表
     */
    private List<Message> messages;
    
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
     * 运行ID
     */
    private String runId;
    
    /**
     * 元数据
     */
    private Map<String, Object> metadata;
    
    /**
     * 是否进行推理
     */
    @Builder.Default
    private Boolean infer = true;
    
    /**
     * 记忆类型
     */
    @Builder.Default
    private String memoryType = "fact";
    
    /**
     * 自定义提示词
     */
    private String prompt;
    
    /**
     * 消息对象
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        /**
         * 角色：user, assistant, system
         */
        private String role;
        
        /**
         * 消息内容
         */
        private String content;
        
        /**
         * 发送者名称
         */
        private String name;
    }
} 