package com.mem0.sdk;

import com.mem0.sdk.config.Mem0Config;
import com.mem0.sdk.dto.*;
import com.mem0.sdk.exception.Mem0Exception;
import com.mem0.sdk.service.MemoryService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * Mem0 SDK 客户端
 * 专注于记忆管理功能，提供简单易用的 API 接口
 * 
 * @author changyu496
 */
@Slf4j
public class Mem0Client {
    
    private final Mem0Config config;
    private final MemoryService memoryService;
    
    /**
     * 构造函数
     * 
     * @param config SDK 配置
     */
    public Mem0Client(Mem0Config config) {
        this.config = config;
        this.memoryService = new MemoryService(config);
    }
    
    /**
     * 添加记忆（带推理）
     * 
     * @param request 记忆添加请求
     * @return 记忆操作结果列表
     * @throws Mem0Exception 操作异常
     */
    public List<MemoryOperationResult> addMemory(AddMemoryRequest request) throws Mem0Exception {
        try {
            log.debug("Adding memory with inference: {}", request);
            return memoryService.addMemoryWithInference(request);
        } catch (Exception e) {
            log.error("Failed to add memory: {}", e.getMessage(), e);
            throw new Mem0Exception("Failed to add memory", e);
        }
    }
    
    /**
     * 添加记忆（不带推理）
     * 
     * @param request 记忆添加请求
     * @return 记忆操作结果列表
     * @throws Mem0Exception 操作异常
     */
    public List<MemoryOperationResult> addRawMemory(AddMemoryRequest request) throws Mem0Exception {
        try {
            log.debug("Adding raw memory: {}", request);
            request.setInfer(false);
            return memoryService.addMemoryWithInference(request);
        } catch (Exception e) {
            log.error("Failed to add raw memory: {}", e.getMessage(), e);
            throw new Mem0Exception("Failed to add raw memory", e);
        }
    }
    
    /**
     * 搜索记忆
     * 
     * @param request 记忆搜索请求
     * @return 记忆搜索结果
     * @throws Mem0Exception 操作异常
     */
    public SearchMemoryResult searchMemory(SearchMemoryRequest request) throws Mem0Exception {
        try {
            log.debug("Searching memory: {}", request);
            return memoryService.searchMemory(request);
        } catch (Exception e) {
            log.error("Failed to search memory: {}", e.getMessage(), e);
            throw new Mem0Exception("Failed to search memory", e);
        }
    }
    
    /**
     * 获取记忆
     * 
     * @param memoryId 记忆ID
     * @return 记忆信息
     * @throws Mem0Exception 操作异常
     */
    public MemoryInfo getMemory(String memoryId) throws Mem0Exception {
        try {
            log.debug("Getting memory: {}", memoryId);
            return memoryService.getMemory(memoryId);
        } catch (Exception e) {
            log.error("Failed to get memory: {}", e.getMessage(), e);
            throw new Mem0Exception("Failed to get memory", e);
        }
    }
    
    /**
     * 更新记忆
     * 
     * @param memoryId 记忆ID
     * @param request 更新请求
     * @return 更新后的记忆信息
     * @throws Mem0Exception 操作异常
     */
    public MemoryInfo updateMemory(String memoryId, UpdateMemoryRequest request) throws Mem0Exception {
        try {
            log.debug("Updating memory: {} with {}", memoryId, request);
            return memoryService.updateMemory(memoryId, request);
        } catch (Exception e) {
            log.error("Failed to update memory: {}", e.getMessage(), e);
            throw new Mem0Exception("Failed to update memory", e);
        }
    }
    
    /**
     * 删除记忆
     * 
     * @param memoryId 记忆ID
     * @throws Mem0Exception 操作异常
     */
    public void deleteMemory(String memoryId) throws Mem0Exception {
        try {
            log.debug("Deleting memory: {}", memoryId);
            memoryService.deleteMemory(memoryId);
        } catch (Exception e) {
            log.error("Failed to delete memory: {}", e.getMessage(), e);
            throw new Mem0Exception("Failed to delete memory", e);
        }
    }
    
    /**
     * 获取用户记忆列表
     * 
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 记忆列表
     * @throws Mem0Exception 操作异常
     */
    public List<MemoryInfo> getUserMemories(String userId, int limit) throws Mem0Exception {
        try {
            log.debug("Getting user memories: userId={}, limit={}", userId, limit);
            return memoryService.getUserMemories(userId, limit);
        } catch (Exception e) {
            log.error("Failed to get user memories: {}", e.getMessage(), e);
            throw new Mem0Exception("Failed to get user memories", e);
        }
    }
    
    /**
     * 获取智能体记忆列表
     * 
     * @param agentId 智能体ID
     * @param limit 限制数量
     * @return 记忆列表
     * @throws Mem0Exception 操作异常
     */
    public List<MemoryInfo> getAgentMemories(String agentId, int limit) throws Mem0Exception {
        try {
            log.debug("Getting agent memories: agentId={}, limit={}", agentId, limit);
            return memoryService.getAgentMemories(agentId, limit);
        } catch (Exception e) {
            log.error("Failed to get agent memories: {}", e.getMessage(), e);
            throw new Mem0Exception("Failed to get agent memories", e);
        }
    }
    
    /**
     * 分页查询记忆
     * 
     * @param request 分页查询请求
     * @return 分页查询结果
     * @throws Mem0Exception 操作异常
     */
    public PageResult<MemoryInfo> getMemoriesWithPagination(PaginationRequest request) throws Mem0Exception {
        try {
            log.debug("Getting memories with pagination: {}", request);
            return memoryService.getMemoriesWithPagination(request);
        } catch (Exception e) {
            log.error("Failed to get memories with pagination: {}", e.getMessage(), e);
            throw new Mem0Exception("Failed to get memories with pagination", e);
        }
    }
    
    /**
     * 统计记忆数量
     * 
     * @param conditions 统计条件
     * @return 记忆数量
     * @throws Mem0Exception 操作异常
     */
    public long countMemories(Map<String, Object> conditions) throws Mem0Exception {
        try {
            log.debug("Counting memories with conditions: {}", conditions);
            return memoryService.countMemories(conditions);
        } catch (Exception e) {
            log.error("Failed to count memories: {}", e.getMessage(), e);
            throw new Mem0Exception("Failed to count memories", e);
        }
    }
    
    /**
     * 健康检查
     * 
     * @return 健康状态
     * @throws Mem0Exception 操作异常
     */
    public boolean healthCheck() throws Mem0Exception {
        try {
            log.debug("Performing health check");
            return memoryService.healthCheck();
        } catch (Exception e) {
            log.error("Health check failed: {}", e.getMessage(), e);
            throw new Mem0Exception("Health check failed", e);
        }
    }
    
    /**
     * 获取配置信息
     * 
     * @return SDK 配置
     */
    public Mem0Config getConfig() {
        return config;
    }
} 