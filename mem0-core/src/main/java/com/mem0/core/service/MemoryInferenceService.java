package com.mem0.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mem0.core.entity.Memory;
import com.mem0.core.prompt.PromptService;
import com.mem0.core.vector.VectorStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.stereotype.Service;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 记忆推理服务 - 实现原版mem0的核心LLM推理逻辑
 * 
 * @author changyu496
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemoryInferenceService {
    
    private final ChatModel chatModel;
    private final EmbeddingModel embeddingModel;
    private final PromptService promptService;
    private final VectorStoreService vectorStoreService;
    private final MemoryService memoryService;
    private final ObjectMapper objectMapper;
    
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    
    /**
     * 添加记忆（带推理）
     * 
     * @param messages 消息列表
     * @param metadata 元数据
     * @param filters 过滤条件
     * @param infer 是否进行推理
     * @return 记忆操作结果
     */
    public List<MemoryOperationResult> addMemoryWithInference(
            List<Map<String, String>> messages,
            Map<String, Object> metadata,
            Map<String, Object> filters,
            boolean infer) {
        
        if (!infer) {
            // 直接添加，不进行推理
            return addRawMessages(messages, metadata);
        }
        
        try {
            // 1. 解析消息
            String parsedMessages = parseMessages(messages);
            
            // 2. 提取事实
            List<String> newFacts = extractFacts(parsedMessages);
            
            if (newFacts.isEmpty()) {
                log.debug("No new facts retrieved from input. Skipping memory update LLM call.");
                return new ArrayList<>();
            }
            
            // 3. 搜索相关记忆
            List<Map<String, String>> retrievedOldMemory = searchRelatedMemories(newFacts, filters);
            
            // 4. 推理记忆操作
            List<MemoryOperationResult> results = inferMemoryOperations(retrievedOldMemory, newFacts);
            
            // 5. 执行记忆操作
            return executeMemoryOperations(results, metadata);
            
        } catch (Exception e) {
            log.error("Error in memory inference: ", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 直接添加原始消息
     */
    private List<MemoryOperationResult> addRawMessages(List<Map<String, String>> messages, Map<String, Object> metadata) {
        List<MemoryOperationResult> results = new ArrayList<>();
        
        for (Map<String, String> message : messages) {
            if (!isValidMessage(message)) {
                log.warn("Skipping invalid message format: {}", message);
                continue;
            }
            
            if ("system".equals(message.get("role"))) {
                continue;
            }
            
            try {
                // 创建记忆
                Memory memory = new Memory();
                memory.setContent(message.get("content"));
                
                // 构建完整的metadata，合并传入的metadata和消息中的信息
                Map<String, Object> fullMetadata = new HashMap<>();
                if (metadata != null) {
                    fullMetadata.putAll(metadata);
                }
                
                // 从消息中提取额外信息并添加到metadata
                String actorName = message.get("name");
                if (actorName != null) {
                    fullMetadata.put("actor_name", actorName);
                }
                
                String role = message.get("role");
                if (role != null) {
                    fullMetadata.put("role", role);
                }
                
                // 设置时间戳
                fullMetadata.put("timestamp", System.currentTimeMillis());
                
                // 将metadata转换为JSON字符串
                memory.setMetadata(objectMapper.writeValueAsString(fullMetadata));
                
                // 设置其他必要字段
                memory.setMemoryId(UUID.randomUUID().toString()); // 生成唯一ID
                memory.setMemoryType("fact");
                memory.setVersion(1);
                memory.setDefaults(); // 设置默认值

                // 从metadata中提取必填字段
                if (metadata != null) {
                    if (metadata.get("app_id") != null) {
                        memory.setAppId(Long.valueOf(metadata.get("app_id").toString()));
                    }
                    if (metadata.get("agent_id") != null) {
                        memory.setAgentId(Long.valueOf(metadata.get("agent_id").toString()));
                    }
                    if (metadata.get("user_id") != null) {
                        memory.setUserId(Long.valueOf(metadata.get("user_id").toString()));
                    }
                    // Memory实体没有runId字段，runId信息会存储在metadata中
                }
                
                Memory savedMemory = memoryService.createMemory(memory);
                
                // 同时将向量存储到Elasticsearch
                try {
                    // 获取记忆内容的向量嵌入
                    List<Float> embeddings = vectorStoreService.getEmbeddings(memory.getContent());
                    
                    // 将向量存储到Elasticsearch
                    vectorStoreService.storeMemoryVector(savedMemory, embeddings);
                    
                    log.info("Successfully stored memory vector for memory ID: {}", savedMemory.getId());
                } catch (Exception e) {
                    log.error("Failed to store memory vector for memory ID: {}, but memory was saved to database", savedMemory.getId(), e);
                    // 注意：即使向量存储失败，也不应该回滚数据库操作，因为记忆已经成功保存
                }
                
                MemoryOperationResult result = new MemoryOperationResult();
                result.setId(savedMemory.getId().toString());
                result.setMemory(message.get("content"));
                result.setEvent("ADD");
                result.setActorId(actorName);
                result.setRole(role);
                
                results.add(result);
                
            } catch (Exception e) {
                log.error("Error creating memory for message: {}", message, e);
            }
        }
        
        return results;
    }
    
    /**
     * 解析消息
     */
    private String parseMessages(List<Map<String, String>> messages) {
        return messages.stream()
                .filter(msg -> msg.get("content") != null && !"system".equals(msg.get("role")))
                .map(msg -> msg.get("content"))
                .collect(Collectors.joining("\n"));
    }
    
    /**
     * 提取事实
     */
    private List<String> extractFacts(String parsedMessages) {
        try {
            String[] prompts = promptService.getFactRetrievalMessages(parsedMessages);
            
            // 使用spring-ai-qwen-example的方式调用ChatModel
            String fullPrompt = prompts[0] + "\n\n" + prompts[1];
            
            // 创建聊天选项
            DashScopeChatOptions options = DashScopeChatOptions.builder()
                    .withModel("qwen-plus")
                    .withTemperature(0.7)
                    .build();
            
            ChatResponse response = chatModel.call(new Prompt(fullPrompt, options));
            String responseContent = response.getResult().getOutput().getText();
            
            // 清理响应内容
            String cleanResponse = removeCodeBlocks(responseContent);
            
            // 解析JSON
            JsonNode jsonNode = objectMapper.readTree(cleanResponse);
            JsonNode factsNode = jsonNode.get("facts");
            
            if (factsNode != null && factsNode.isArray()) {
                List<String> facts = new ArrayList<>();
                for (JsonNode fact : factsNode) {
                    facts.add(fact.asText());
                }
                return facts;
            }
            
        } catch (Exception e) {
            log.error("Error extracting facts: ", e);
        }
        
        return new ArrayList<>();
    }
    
    /**
     * 搜索相关记忆
     */
    private List<Map<String, String>> searchRelatedMemories(List<String> newFacts, Map<String, Object> filters) {
        List<Map<String, String>> retrievedOldMemory = new ArrayList<>();
        Map<String, String> uniqueData = new HashMap<>();
        
        for (String fact : newFacts) {
            try {
                // 获取向量嵌入
                List<Float> embeddings = vectorStoreService.getEmbeddings(fact);
                
                // 搜索相似记忆
                List<Memory> similarMemories = vectorStoreService.searchSimilar(fact, embeddings, 5, filters);
                
                for (Memory memory : similarMemories) {
                    log.info("找到相似记忆 - ID: {}, Content: {}", memory.getId(), memory.getContent());
                    Map<String, String> memoryInfo = new HashMap<>();
                    memoryInfo.put("id", memory.getId().toString());
                    memoryInfo.put("text", memory.getContent());
                    uniqueData.put(memory.getId().toString(), memory.getContent());
                }
                
            } catch (Exception e) {
                log.error("Error searching related memories for fact: {}", fact, e);
            }
        }
        
        // 去重
        for (Map.Entry<String, String> entry : uniqueData.entrySet()) {
            Map<String, String> memoryInfo = new HashMap<>();
            memoryInfo.put("id", entry.getKey());
            memoryInfo.put("text", entry.getValue());
            retrievedOldMemory.add(memoryInfo);
        }
        
        log.info("Total existing memories: {}", retrievedOldMemory.size());
        return retrievedOldMemory;
    }
    
    /**
     * 推理记忆操作
     */
    private List<MemoryOperationResult> inferMemoryOperations(
            List<Map<String, String>> retrievedOldMemory,
            List<String> newRetrievedFacts) {
        
        if (newRetrievedFacts.isEmpty()) {
            return new ArrayList<>();
        }
        
        try {
            // 创建UUID映射（处理UUID幻觉）
            Map<String, String> tempUuidMapping = new HashMap<>();
            List<Map<String, String>> mappedOldMemory = new ArrayList<>();
            
            for (int i = 0; i < retrievedOldMemory.size(); i++) {
                Map<String, String> memory = retrievedOldMemory.get(i);
                String tempId = String.valueOf(i);
                tempUuidMapping.put(tempId, memory.get("id"));
                
                Map<String, String> mappedMemory = new HashMap<>();
                mappedMemory.put("id", tempId);
                mappedMemory.put("text", memory.get("text"));
                mappedOldMemory.add(mappedMemory);
            }
            
            // 生成推理提示词
            String functionCallingPrompt = promptService.getUpdateMemoryMessages(
                    mappedOldMemory, newRetrievedFacts, null);
            
            log.info("推理Prompt: {}", functionCallingPrompt);
            
            // 创建聊天选项
            DashScopeChatOptions options = DashScopeChatOptions.builder()
                    .withModel("qwen-plus")
                    .withTemperature(0.7)
                    .build();
            
            // 使用spring-ai-qwen-example的方式调用ChatModel
            ChatResponse response = chatModel.call(new Prompt(functionCallingPrompt, options));
            String responseContent = response.getResult().getOutput().getText();
            
            log.info("LLM原始响应: {}", responseContent);
            
            // 清理响应内容
            String cleanResponse = removeCodeBlocks(responseContent);
            log.info("LLM清理后响应: {}", cleanResponse);
            
            // 解析JSON
            JsonNode jsonNode = objectMapper.readTree(cleanResponse);
            JsonNode memoryNode = jsonNode.get("memory");
            
            List<MemoryOperationResult> results = new ArrayList<>();
            
            if (memoryNode != null && memoryNode.isArray()) {
                for (JsonNode memoryItem : memoryNode) {
                    try {
                        String eventType = memoryItem.get("event").asText();
                        // 尝试获取memory字段，如果不存在则尝试text字段
                        JsonNode memoryTextNode = memoryItem.get("memory");
                        if (memoryTextNode == null) {
                            memoryTextNode = memoryItem.get("text");
                        }
                        String memoryText = memoryTextNode != null ? memoryTextNode.asText() : "";
                        String id = memoryItem.get("id").asText();
                        
                        MemoryOperationResult result = new MemoryOperationResult();
                        result.setMemory(memoryText);
                        result.setEvent(eventType);
                        
                        if ("UPDATE".equals(eventType) || "DELETE".equals(eventType)) {
                            // 使用原始UUID
                            result.setId(tempUuidMapping.get(id));
                            if ("UPDATE".equals(eventType)) {
                                JsonNode oldMemoryNode = memoryItem.get("old_memory");
                                if (oldMemoryNode != null) {
                                    result.setPreviousMemory(oldMemoryNode.asText());
                                }
                            }
                        }
                        
                        results.add(result);
                        
                    } catch (Exception e) {
                        log.error("Error processing memory action: {}", memoryItem, e);
                    }
                }
            }
            
            return results;
            
        } catch (Exception e) {
            log.error("Error inferring memory operations: ", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 执行记忆操作
     */
    private List<MemoryOperationResult> executeMemoryOperations(
            List<MemoryOperationResult> operations,
            Map<String, Object> metadata) {
        
        List<MemoryOperationResult> results = new ArrayList<>();
        
        for (MemoryOperationResult operation : operations) {
            try {
                switch (operation.getEvent()) {
                    case "ADD":
                        // 创建新记忆
                        Memory newMemory = new Memory();
                        newMemory.setContent(operation.getMemory());
                        newMemory.setMetadata(objectMapper.writeValueAsString(metadata));
                        newMemory.setMemoryId(UUID.randomUUID().toString()); // 生成唯一ID
                        newMemory.setMemoryType("fact");
                        newMemory.setVersion(1);
                        
                        // 从metadata中提取必填字段
                        if (metadata != null) {
                            if (metadata.get("app_id") != null) {
                                newMemory.setAppId(Long.valueOf(metadata.get("app_id").toString()));
                            }
                            if (metadata.get("agent_id") != null) {
                                newMemory.setAgentId(Long.valueOf(metadata.get("agent_id").toString()));
                            }
                            if (metadata.get("user_id") != null) {
                                newMemory.setUserId(Long.valueOf(metadata.get("user_id").toString()));
                            }
                            // Memory实体没有runId字段，runId信息会存储在metadata中
                        }
                        
                        newMemory.setDefaults(); // 设置默认值
                        
                        Memory savedMemory = memoryService.createMemory(newMemory);
                        operation.setId(savedMemory.getId().toString());
                        
                        // 同时将向量存储到Elasticsearch
                        try {
                            List<Float> embeddings = vectorStoreService.getEmbeddings(newMemory.getContent());
                            vectorStoreService.storeMemoryVector(savedMemory, embeddings);
                            log.info("Successfully stored memory vector for memory ID: {}", savedMemory.getId());
                        } catch (Exception e) {
                            log.error("Failed to store memory vector for memory ID: {}, but memory was saved to database", savedMemory.getId(), e);
                        }
                        
                        results.add(operation);
                        break;
                        
                    case "UPDATE":
                        // 更新现有记忆
                        if (operation.getId() != null) {
                            Memory existingMemory = memoryService.getMemoryById(Long.valueOf(operation.getId()));
                            if (existingMemory != null) {
                                String oldContent = existingMemory.getContent();
                                existingMemory.setContent(operation.getMemory());
                                memoryService.updateMemory(existingMemory);
                                
                                // 同时更新向量存储
                                try {
                                    List<Float> embeddings = vectorStoreService.getEmbeddings(operation.getMemory());
                                    Map<String, Object> metadataMap = objectMapper.readValue(existingMemory.getMetadata(), Map.class);
                                    vectorStoreService.updateMemoryVector(existingMemory.getId().toString(), operation.getMemory(), embeddings, metadataMap);
                                    log.info("Successfully updated memory vector for memory ID: {}", existingMemory.getId());
                                } catch (Exception e) {
                                    log.error("Failed to update memory vector for memory ID: {}, but memory was updated in database", existingMemory.getId(), e);
                                }
                                
                                results.add(operation);
                            }
                        }
                        break;
                        
                    case "DELETE":
                        // 删除记忆
                        if (operation.getId() != null) {
                            // 先删除向量存储
                            try {
                                vectorStoreService.deleteMemoryVector(operation.getId());
                                log.info("Successfully deleted memory vector for memory ID: {}", operation.getId());
                            } catch (Exception e) {
                                log.error("Failed to delete memory vector for memory ID: {}, but will continue with database deletion", operation.getId(), e);
                            }
                            
                            // 再删除数据库记录
                            memoryService.deleteMemory(Long.valueOf(operation.getId()));
                            results.add(operation);
                        }
                        break;
                        
                    case "NONE":
                        // 无操作，但仍然返回结果表示没有变化
                        results.add(operation);
                        break;
                        
                    default:
                        log.warn("Unknown event type: {}", operation.getEvent());
                }
                
            } catch (Exception e) {
                log.error("Error executing memory operation: {}", operation, e);
            }
        }
        
        return results;
    }
    
    /**
     * 验证消息格式
     */
    private boolean isValidMessage(Map<String, String> message) {
        return message != null && 
               message.get("role") != null && 
               message.get("content") != null;
    }
    
    /**
     * 移除代码块标记
     */
    private String removeCodeBlocks(String content) {
        if (content == null) {
            return "";
        }
        
        // 移除 ```json 和 ``` 标记
        return content.replaceAll("```json\\s*", "")
                     .replaceAll("```\\s*", "")
                     .trim();
    }
    
    /**
     * 记忆操作结果
     */
    public static class MemoryOperationResult {
        private String id;
        private String memory;
        private String event;
        private String actorId;
        private String role;
        private String previousMemory;
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getMemory() { return memory; }
        public void setMemory(String memory) { this.memory = memory; }
        
        public String getEvent() { return event; }
        public void setEvent(String event) { this.event = event; }
        
        public String getActorId() { return actorId; }
        public void setActorId(String actorId) { this.actorId = actorId; }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        
        public String getPreviousMemory() { return previousMemory; }
        public void setPreviousMemory(String previousMemory) { this.previousMemory = previousMemory; }
    }
} 