package com.mem0.sdk.config;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Mem0 SDK 配置类
 * 
 * @author changyu496
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mem0Config {
    
    /**
     * 服务器地址，例如：http://localhost:8080
     */
    private String serverUrl;
    
    /**
     * 连接超时时间（毫秒）
     */
    @Builder.Default
    private int connectTimeout = 5000;
    
    /**
     * 读取超时时间（毫秒）
     */
    @Builder.Default
    private int readTimeout = 30000;
    
    /**
     * 是否启用重试
     */
    @Builder.Default
    private boolean enableRetry = true;
    
    /**
     * 最大重试次数
     */
    @Builder.Default
    private int maxRetries = 3;
    
    /**
     * 重试间隔（毫秒）
     */
    @Builder.Default
    private long retryInterval = 1000;
    
    /**
     * 是否启用日志
     */
    @Builder.Default
    private boolean enableLogging = true;
    
    /**
     * 日志级别
     */
    @Builder.Default
    private String logLevel = "INFO";
    
    /**
     * 用户代理
     */
    @Builder.Default
    private String userAgent = "Mem0-Java-SDK/1.0.0";
    
    /**
     * 默认应用ID
     */
    private String defaultAppId;
    
    /**
     * 默认智能体ID
     */
    private String defaultAgentId;
    
    /**
     * 默认用户ID
     */
    private String defaultUserId;
    
    /**
     * 验证配置是否有效
     * 
     * @throws IllegalArgumentException 配置无效时抛出异常
     */
    public void validate() {
        if (serverUrl == null || serverUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Server URL cannot be null or empty");
        }
        
        if (!serverUrl.startsWith("http://") && !serverUrl.startsWith("https://")) {
            throw new IllegalArgumentException("Server URL must start with http:// or https://");
        }
        
        if (connectTimeout <= 0) {
            throw new IllegalArgumentException("Connect timeout must be positive");
        }
        
        if (readTimeout <= 0) {
            throw new IllegalArgumentException("Read timeout must be positive");
        }
        
        if (maxRetries < 0) {
            throw new IllegalArgumentException("Max retries cannot be negative");
        }
        
        if (retryInterval < 0) {
            throw new IllegalArgumentException("Retry interval cannot be negative");
        }
    }
    
    /**
     * 创建默认配置
     * 
     * @param serverUrl 服务器地址
     * @return 默认配置
     */
    public static Mem0Config defaultConfig(String serverUrl) {
        return Mem0Config.builder()
                .serverUrl(serverUrl)
                .build();
    }
    
    /**
     * 创建生产环境配置
     * 
     * @param serverUrl 服务器地址
     * @return 生产环境配置
     */
    public static Mem0Config productionConfig(String serverUrl) {
        return Mem0Config.builder()
                .serverUrl(serverUrl)
                .connectTimeout(10000)
                .readTimeout(60000)
                .enableRetry(true)
                .maxRetries(5)
                .retryInterval(2000)
                .enableLogging(false)
                .logLevel("WARN")
                .build();
    }
    
    /**
     * 创建开发环境配置
     * 
     * @param serverUrl 服务器地址
     * @return 开发环境配置
     */
    public static Mem0Config developmentConfig(String serverUrl) {
        return Mem0Config.builder()
                .serverUrl(serverUrl)
                .connectTimeout(3000)
                .readTimeout(15000)
                .enableRetry(true)
                .maxRetries(2)
                .retryInterval(500)
                .enableLogging(true)
                .logLevel("DEBUG")
                .build();
    }
} 