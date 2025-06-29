package com.mem0.core.memory;

/**
 * 记忆类型枚举
 * 
 * @author changyu496
 */
public enum MemoryType {
    /**
     * 事实记忆：存储对话中的事实信息
     */
    FACT("fact", "事实记忆"),
    
    /**
     * 程序记忆：存储操作步骤和流程
     */
    PROCEDURAL("procedural", "程序记忆");
    
    private final String code;
    private final String description;
    
    MemoryType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据代码获取记忆类型
     */
    public static MemoryType fromCode(String code) {
        for (MemoryType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown memory type code: " + code);
    }
} 