package com.mem0.server.controller;

import com.mem0.core.dto.PageResult;
import com.mem0.core.entity.Agent;
import com.mem0.core.entity.App;
import com.mem0.core.entity.Memory;
import com.mem0.core.service.AgentService;
import com.mem0.core.service.AppService;
import com.mem0.core.service.MemoryInferenceService;
import com.mem0.core.service.MemoryService;
import com.mem0.core.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingOptions;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 测试控制器
 * 
 * @author changyu496
 */
@Slf4j
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
    
    private final ChatModel chatModel;
    private final EmbeddingModel embeddingModel;
    private final AppService appService;
    private final AgentService agentService;
    private final MemoryService memoryService;
    private final MemoryInferenceService memoryInferenceService;
    private final com.mem0.core.vector.VectorStoreService vectorStoreService;
    
    /**
     * 测试聊天功能
     */
    @PostMapping("/chat")
    public Map<String, Object> chat(@RequestBody Map<String, String> request) {
        try {
            String message = request.get("message");
            // 创建聊天选项
            DashScopeChatOptions options = DashScopeChatOptions.builder()
                    .withModel("qwen-plus")
                    .withTemperature(0.7)
                    .build();
            // 调用聊天模型
            ChatResponse response = chatModel.call(new Prompt(message, options));
            String responseContent = response.getResult().getOutput().getText();
            Map<String, Object> result = new HashMap<>();
            result.put("message", message);
            result.put("response", responseContent);
            result.put("timestamp", new Date());
            return result;
        } catch (Exception e) {
            log.error("Chat error: ", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return error;
        }
    }
    
    /**
     * 测试嵌入功能
     */
    @PostMapping("/embed")
    public Map<String, Object> embed(@RequestBody Map<String, String> request) {
        try {
            String text = request.get("text");
            // 创建嵌入选项
            DashScopeEmbeddingOptions options = DashScopeEmbeddingOptions.builder()
                    .withModel("text-embedding-v1")
                    .build();
            // 获取嵌入向量
            EmbeddingResponse embeddingResponse = embeddingModel.call(new EmbeddingRequest(
                List.of(text), options
            ));
            float[] embeddings = embeddingResponse.getResult().getOutput();
            List<Float> embeddingList = new ArrayList<>();
            for (float f : embeddings) {
                embeddingList.add(f);
            }
            Map<String, Object> result = new HashMap<>();
            result.put("text", text);
            result.put("embeddings", embeddingList);
            result.put("dimensions", embeddingList.size());
            result.put("timestamp", new Date());
            return result;
        } catch (Exception e) {
            log.error("Embedding error: ", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return error;
        }
    }
    
    /**
     * 测试应用创建
     */
    @PostMapping("/app")
    public App createApp(@RequestBody Map<String, String> request) {
        App app = new App();
        app.setAppKey(request.get("appKey"));
        app.setAppName(request.get("appName"));
        app.setDescription(request.get("description"));
        app.setDefaults(); // 设置默认值
        return appService.create(app);
    }
    
    /**
     * 测试智能体创建
     */
    @PostMapping("/agent")
    public Agent createAgent(@RequestBody Map<String, String> request) {
        Agent agent = new Agent();
        agent.setAgentId(request.get("agentId"));
        agent.setAgentName(request.get("agentName"));
        agent.setDescription(request.get("description"));
        agent.setAppId(Long.valueOf(request.get("appId")));
        agent.setDefaults(); // 设置默认值
        return agentService.create(agent);
    }
    
    /**
     * 测试记忆添加（带推理）
     */
    @PostMapping("/memory/add")
    public List<MemoryInferenceService.MemoryOperationResult> addMemory(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Map<String, String>> messages = (List<Map<String, String>>) request.get("messages");
        String userId = (String) request.get("userId");
        String agentId = (String) request.get("agentId");
        String runId = (String) request.get("runId");
        String appId = (String) request.get("appId");
        @SuppressWarnings("unchecked")
        Map<String, Object> metadata = (Map<String, Object>) request.get("metadata");
        Boolean infer = (Boolean) request.get("infer");
        String memoryType = (String) request.get("memoryType");
        String prompt = (String) request.get("prompt");
        if (infer == null) {
            infer = true; // 默认进行推理
        }
        
        // 构建过滤条件
        Map<String, Object> filters = new HashMap<>();
        if (userId != null) {
            filters.put("user_id", userId);
        }
        if (agentId != null) {
            filters.put("agent_id", agentId);
        }
        if (appId != null) {
            filters.put("app_id", appId);
        }
        
        // 处理元数据
        Map<String, Object> processedMetadata = new HashMap<>();
        if (metadata != null) {
            processedMetadata.putAll(metadata);
        }
        if (userId != null) {
            processedMetadata.put("user_id", userId);
        }
        if (agentId != null) {
            processedMetadata.put("agent_id", agentId);
        }
        if (runId != null) {
            processedMetadata.put("run_id", runId);
        }
        if (appId != null) {
            processedMetadata.put("app_id", appId);
        }
        
        // 调用推理服务
        return memoryInferenceService.addMemoryWithInference(messages, processedMetadata, filters, infer);
    }
    
    /**
     * 测试记忆查询
     */
    @GetMapping("/memory/search")
    public List<Memory> searchMemories(@RequestParam String userId, 
                                      @RequestParam(required = false) String agentId,
                                      @RequestParam(required = false) String runId) {
        Map<String, Object> conditions = new HashMap<>();
        try {
            conditions.put("userId", Long.valueOf(userId));
            if (agentId != null) {
                conditions.put("agentId", Long.valueOf(agentId));
            }
        } catch (NumberFormatException e) {
            // 如果转换失败，使用字符串类型
            conditions.put("userId", userId);
            if (agentId != null) {
                conditions.put("agentId", agentId);
            }
        }
        // runId 是字符串类型，不需要转换，但需要从metadata中查询
        return memoryService.getMemoriesByConditions(conditions);
    }
    
    /**
     * 测试记忆分页查询
     */
    @GetMapping("/memory/page")
    public PageResult<Memory> getMemoriesPage(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "20") int size,
                                             @RequestParam(required = false) String userId,
                                             @RequestParam(required = false) String agentId,
                                             @RequestParam(required = false) String appId,
                                             @RequestParam(required = false) String memoryType) {
        Map<String, Object> conditions = new HashMap<>();
        if (userId != null) {
            conditions.put("userId", Long.valueOf(userId));
        }
        if (agentId != null) {
            conditions.put("agentId", Long.valueOf(agentId));
        }
        if (appId != null) {
            conditions.put("appId", Long.valueOf(appId));
        }
        if (memoryType != null) {
            conditions.put("memoryType", memoryType);
        }
        
        return memoryService.getMemoriesPage(conditions, page, size);
    }
    
    /**
     * 测试记忆统计
     */
    @GetMapping("/memory/count")
    public Map<String, Object> countMemories(@RequestParam(required = false) String userId,
                                            @RequestParam(required = false) String agentId,
                                            @RequestParam(required = false) String appId,
                                            @RequestParam(required = false) String memoryType) {
        Map<String, Object> result = new HashMap<>();
        
        if (userId == null && agentId == null && appId == null && memoryType == null) {
            // 统计总数
            long total = memoryService.countMemories();
            result.put("total", total);
        } else {
            // 根据条件统计
            Map<String, Object> conditions = new HashMap<>();
            if (userId != null) {
                conditions.put("userId", Long.valueOf(userId));
            }
            if (agentId != null) {
                conditions.put("agentId", Long.valueOf(agentId));
            }
            if (appId != null) {
                conditions.put("appId", Long.valueOf(appId));
            }
            if (memoryType != null) {
                conditions.put("memoryType", memoryType);
            }
            
            long count = memoryService.countMemoriesByConditions(conditions);
            result.put("count", count);
            result.put("conditions", conditions);
        }
        
        result.put("timestamp", new Date());
        return result;
    }
    
    /**
     * 测试记忆获取
     */
    @GetMapping("/memory/{id}")
    public Memory getMemory(@PathVariable Long id) {
        return memoryService.getMemoryById(id);
    }
    
    /**
     * 测试记忆更新
     */
    @PutMapping("/memory/{id}")
    public void updateMemory(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Memory memory = memoryService.getMemoryById(id);
        if (memory != null) {
            memory.setContent((String) request.get("data"));
            @SuppressWarnings("unchecked")
            Map<String, Object> metadata = (Map<String, Object>) request.get("metadata");
            memory.setMetadata(JsonUtil.mapToJson(metadata));
            memoryService.updateMemory(memory);
        }
    }
    
    /**
     * 测试记忆删除
     */
    @DeleteMapping("/memory/{id}")
    public void deleteMemory(@PathVariable String id) {
        memoryService.deleteMemory(Long.valueOf(id));
    }
    
    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "ok");
        result.put("timestamp", new Date());
        return result;
    }
    
    /**
     * 测试向量搜索
     */
    @PostMapping("/vector/search")
    public Map<String, Object> searchVectors(@RequestBody Map<String, Object> request) {
        try {
            String query = (String) request.get("query");
            Integer limit = (Integer) request.get("limit");
            if (limit == null) {
                limit = 10;
            }
            
            // 获取查询文本的嵌入向量
            DashScopeEmbeddingOptions options = DashScopeEmbeddingOptions.builder()
                    .withModel("text-embedding-v1")
                    .build();
            EmbeddingResponse embeddingResponse = embeddingModel.call(new EmbeddingRequest(
                List.of(query), options
            ));
            float[] embeddings = embeddingResponse.getResult().getOutput();
            List<Float> queryVector = new ArrayList<>();
            for (float f : embeddings) {
                queryVector.add(f);
            }
            
            // 执行向量搜索
            List<com.mem0.core.entity.Memory> results = 
                vectorStoreService.searchMemories(query, limit, null);
            
            Map<String, Object> result = new HashMap<>();
            result.put("query", query);
            result.put("results", results);
            result.put("count", results.size());
            result.put("timestamp", new Date());
            return result;
        } catch (Exception e) {
            log.error("Vector search error: ", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return error;
        }
    }
} 