package com.mem0.sdk.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mem0.sdk.config.Mem0Config;
import com.mem0.sdk.dto.*;
import com.mem0.sdk.exception.Mem0Exception;
import com.mem0.sdk.http.Mem0HttpClient;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * 记忆服务实现类
 * 
 * @author changyu496
 */
@Slf4j
public class MemoryService {
    
    private final Mem0Config config;
    private final Mem0HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    /**
     * 构造函数
     * 
     * @param config SDK 配置
     */
    public MemoryService(Mem0Config config) {
        this.config = config;
        this.httpClient = new Mem0HttpClient(config);
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * 添加记忆（带推理）
     * 
     * @param request 记忆添加请求
     * @return 记忆操作结果列表
     * @throws Mem0Exception 操作异常
     */
    public List<MemoryOperationResult> addMemoryWithInference(AddMemoryRequest request) throws Mem0Exception {
        try {
            String url = config.getServerUrl() + "/test/memory/add";
            String jsonBody = objectMapper.writeValueAsString(request);
            
            String response = httpClient.post(url, jsonBody);
            return objectMapper.readValue(response, new TypeReference<List<MemoryOperationResult>>() {});
            
        } catch (Exception e) {
            log.error("Failed to add memory with inference: {}", e.getMessage(), e);
            throw Mem0Exception.serverError("Failed to add memory with inference", e);
        }
    }
    
    /**
     * 搜索记忆
     * 
     * @param request 搜索请求
     * @return 搜索结果
     * @throws Mem0Exception 操作异常
     */
    public SearchMemoryResult searchMemory(SearchMemoryRequest request) throws Mem0Exception {
        try {
            // 构建查询参数
            StringBuilder urlBuilder = new StringBuilder(config.getServerUrl() + "/test/memory/search?userId=");
            urlBuilder.append(request.getUserId());
            
            if (request.getAgentId() != null) {
                urlBuilder.append("&agentId=").append(request.getAgentId());
            }
            
            if (request.getAppId() != null) {
                urlBuilder.append("&appId=").append(request.getAppId());
            }
            
            String response = httpClient.get(urlBuilder.toString());
            List<MemoryInfo> memories = objectMapper.readValue(response, new TypeReference<List<MemoryInfo>>() {});
            
            return SearchMemoryResult.builder()
                    .query(request.getQuery())
                    .memories(memories)
                    .total((long) memories.size())
                    .limit(request.getLimit())
                    .includeVectorSearch(request.getIncludeVectorSearch())
                    .similarityThreshold(request.getSimilarityThreshold())
                    .build();
                    
        } catch (Exception e) {
            log.error("Failed to search memory: {}", e.getMessage(), e);
            throw Mem0Exception.serverError("Failed to search memory", e);
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
            String url = config.getServerUrl() + "/test/memory/" + memoryId;
            String response = httpClient.get(url);
            return objectMapper.readValue(response, MemoryInfo.class);
            
        } catch (Exception e) {
            log.error("Failed to get memory: {}", e.getMessage(), e);
            throw Mem0Exception.serverError("Failed to get memory", e);
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
            String url = config.getServerUrl() + "/test/memory/" + memoryId;
            String jsonBody = objectMapper.writeValueAsString(request);
            
            String response = httpClient.put(url, jsonBody);
            return objectMapper.readValue(response, MemoryInfo.class);
            
        } catch (Exception e) {
            log.error("Failed to update memory: {}", e.getMessage(), e);
            throw Mem0Exception.serverError("Failed to update memory", e);
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
            String url = config.getServerUrl() + "/test/memory/" + memoryId;
            httpClient.delete(url);
            
        } catch (Exception e) {
            log.error("Failed to delete memory: {}", e.getMessage(), e);
            throw Mem0Exception.serverError("Failed to delete memory", e);
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
            String url = config.getServerUrl() + "/test/memory/search?userId=" + userId;
            String response = httpClient.get(url);
            List<MemoryInfo> memories = objectMapper.readValue(response, new TypeReference<List<MemoryInfo>>() {});
            
            // 限制数量
            if (memories.size() > limit) {
                return memories.subList(0, limit);
            }
            return memories;
            
        } catch (Exception e) {
            log.error("Failed to get user memories: {}", e.getMessage(), e);
            throw Mem0Exception.serverError("Failed to get user memories", e);
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
            String url = config.getServerUrl() + "/test/memory/search?agentId=" + agentId;
            String response = httpClient.get(url);
            List<MemoryInfo> memories = objectMapper.readValue(response, new TypeReference<List<MemoryInfo>>() {});
            
            // 限制数量
            if (memories.size() > limit) {
                return memories.subList(0, limit);
            }
            return memories;
            
        } catch (Exception e) {
            log.error("Failed to get agent memories: {}", e.getMessage(), e);
            throw Mem0Exception.serverError("Failed to get agent memories", e);
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
            // 构建查询参数
            StringBuilder urlBuilder = new StringBuilder(config.getServerUrl() + "/test/memory/search");
            urlBuilder.append("?userId=").append(request.getUserId());
            
            if (request.getAgentId() != null) {
                urlBuilder.append("&agentId=").append(request.getAgentId());
            }
            
            if (request.getAppId() != null) {
                urlBuilder.append("&appId=").append(request.getAppId());
            }
            
            String response = httpClient.get(urlBuilder.toString());
            List<MemoryInfo> memories = objectMapper.readValue(response, new TypeReference<List<MemoryInfo>>() {});
            
            // 计算分页信息
            int total = memories.size();
            int offset = request.getOffset();
            int limit = request.getLimit();
            
            // 分页处理
            List<MemoryInfo> pagedMemories;
            if (offset >= total) {
                pagedMemories = List.of();
            } else {
                int endIndex = Math.min(offset + limit, total);
                pagedMemories = memories.subList(offset, endIndex);
            }
            
            return PageResult.<MemoryInfo>builder()
                    .data(pagedMemories)
                    .total((long) total)
                    .page(request.getPage())
                    .size(request.getSize())
                    .build();
                    
        } catch (Exception e) {
            log.error("Failed to get memories with pagination: {}", e.getMessage(), e);
            throw Mem0Exception.serverError("Failed to get memories with pagination", e);
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
            // 构建查询参数
            StringBuilder urlBuilder = new StringBuilder(config.getServerUrl() + "/test/memory/search");
            
            if (conditions.containsKey("userId")) {
                urlBuilder.append("?userId=").append(conditions.get("userId"));
            }
            
            if (conditions.containsKey("agentId")) {
                urlBuilder.append(urlBuilder.toString().contains("?") ? "&" : "?")
                         .append("agentId=").append(conditions.get("agentId"));
            }
            
            if (conditions.containsKey("appId")) {
                urlBuilder.append(urlBuilder.toString().contains("?") ? "&" : "?")
                         .append("appId=").append(conditions.get("appId"));
            }
            
            String response = httpClient.get(urlBuilder.toString());
            List<MemoryInfo> memories = objectMapper.readValue(response, new TypeReference<List<MemoryInfo>>() {});
            
            return memories.size();
            
        } catch (Exception e) {
            log.error("Failed to count memories: {}", e.getMessage(), e);
            throw Mem0Exception.serverError("Failed to count memories", e);
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
            String url = config.getServerUrl() + "/test/health";
            String response = httpClient.get(url);
            Map<String, Object> result = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
            
            return "ok".equals(result.get("status"));
            
        } catch (Exception e) {
            log.error("Health check failed: {}", e.getMessage(), e);
            throw Mem0Exception.serverError("Health check failed", e);
        }
    }
} 