package com.mem0.core.llm;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 对话消息类
 * 用于LLM模块中的消息传递
 * 
 * @author changyu496
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    /**
     * 消息角色：user-用户，assistant-助手，system-系统
     */
    private String role;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息时间戳
     */
    private Long timestamp;
    
    /**
     * 消息元数据
     */
    private String metadata;
    
    public Message(String role, String content) {
        this.role = role;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
    }
} 