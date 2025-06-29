package com.mem0.core.vector;

import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingOptions;
import com.mem0.core.entity.Memory;
import com.mem0.core.utils.JsonUtil;
import com.mem0.core.vectorstore.VectorStoreService.VectorData;
import com.mem0.core.vectorstore.VectorStoreService.SearchResult;
import com.mem0.core.vectorstore.impl.ElasticsearchVectorStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 向量存储服务
 * 
 * @author changyu496
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VectorStoreService {
    
    private final EmbeddingModel embeddingModel;
    private final ElasticsearchVectorStoreService elasticsearchVectorStore;
    private static final String INDEX_NAME = "";
    
    /**
     * 获取文本的嵌入向量
     * 
     * @param text 文本内容
     * @return 嵌入向量列表
     */
    public List<Float> getEmbeddings(String text) {
        try {
            // 使用spring-ai-qwen-example的方式调用EmbeddingModel
            EmbeddingResponse response = embeddingModel.call(new EmbeddingRequest(
                List.of(text),
                DashScopeEmbeddingOptions.builder().withModel("text-embedding-v1").build()
            ));
            float[] embeddings = response.getResult().getOutput();
            
            // 转换为List<Float>
            List<Float> result = new java.util.ArrayList<>();
            for (float f : embeddings) {
                result.add(f);
            }
            return result;
        } catch (Exception e) {
            log.error("Error getting embeddings for text: {}", text, e);
            throw new RuntimeException("Failed to get embeddings", e);
        }
    }
    
    /**
     * 搜索相似记忆
     * 
     * @param query 查询文本
     * @param queryEmbeddings 查询向量
     * @param limit 返回数量限制
     * @param filters 过滤条件
     * @return 相似记忆列表
     */
    public List<Memory> searchSimilar(String query, List<Float> queryEmbeddings, int limit, Map<String, Object> filters) {
        try {
            List<SearchResult> searchResults = elasticsearchVectorStore.search(INDEX_NAME, queryEmbeddings, limit, filters);
            return searchResults.stream()
                .map(result -> {
                    Memory memory = new Memory();
                    memory.setId(Long.valueOf(result.getId()));
                    memory.setMetadata(JsonUtil.toJson(result.getMetadata()));
                    
                    // 从metadata中提取content字段
                    Map<String, Object> metadata = result.getMetadata();
                    if (metadata != null && metadata.containsKey("content")) {
                        memory.setContent((String) metadata.get("content"));
                    }
                    
                    return memory;
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error searching similar memories for query: {}", query, e);
            throw new RuntimeException("Failed to search similar memories", e);
        }
    }
    
    /**
     * 存储记忆向量
     * 
     * @param memory 记忆对象
     * @param embeddings 嵌入向量
     */
    public void storeMemoryVector(Memory memory, List<Float> embeddings) {
        try {
            Map<String, Object> metadataMap = JsonUtil.fromJsonToMap(memory.getMetadata());
            // 确保content也被存储到ES中
            metadataMap.put("content", memory.getContent());
            elasticsearchVectorStore.storeVector(INDEX_NAME, memory.getId().toString(), embeddings, metadataMap);
        } catch (Exception e) {
            log.error("Error storing memory vector for memory: {}", memory.getId(), e);
            throw new RuntimeException("Failed to store memory vector", e);
        }
    }
    
    /**
     * 更新记忆向量
     * 
     * @param memoryId 记忆ID
     * @param content 内容
     * @param embeddings 向量嵌入
     * @param metadata 元数据
     */
    public void updateMemoryVector(String memoryId, String content, List<Float> embeddings, Map<String, Object> metadata) {
        try {
            // 先删除旧向量，再存储新向量
            elasticsearchVectorStore.deleteVector(INDEX_NAME, memoryId);
            Map<String, Object> metadataMap = new java.util.HashMap<>();
            metadataMap.put("content", content);
            if (metadata != null) {
                metadataMap.putAll(metadata);
            }
            elasticsearchVectorStore.storeVector(INDEX_NAME, memoryId, embeddings, metadataMap);
        } catch (Exception e) {
            log.error("Error updating memory vector for memory: {}", memoryId, e);
            throw new RuntimeException("Failed to update memory vector", e);
        }
    }
    
    /**
     * 删除记忆向量
     * 
     * @param memoryId 记忆ID
     */
    public void deleteMemoryVector(String memoryId) {
        try {
            elasticsearchVectorStore.deleteVector(INDEX_NAME, memoryId);
        } catch (Exception e) {
            log.error("Error deleting memory vector for memory: {}", memoryId, e);
            throw new RuntimeException("Failed to delete memory vector", e);
        }
    }
    
    /**
     * 搜索记忆
     * 
     * @param query 查询文本
     * @param limit 限制数量
     * @param filters 过滤条件
     * @return 搜索结果
     */
    public List<Memory> searchMemories(String query, int limit, Map<String, Object> filters) {
        try {
            List<Float> embeddings = getEmbeddings(query);
            return searchSimilar(query, embeddings, limit, filters);
        } catch (Exception e) {
            log.error("Error searching memories for query: {}", query, e);
            return List.of();
        }
    }
    
    /**
     * 批量存储记忆向量
     * 
     * @param memories 记忆列表
     */
    public void batchStoreMemoryVectors(List<Memory> memories) {
        for (Memory memory : memories) {
            try {
                List<Float> embeddings = getEmbeddings(memory.getContent());
                storeMemoryVector(memory, embeddings);
            } catch (Exception e) {
                log.error("Error batch storing memory vector for memory: {}", memory.getId(), e);
            }
        }
    }
    
    /**
     * 获取向量维度
     * 
     * @return 向量维度
     */
    public int getEmbeddingDimensions() {
        try {
            // 使用一个简单的测试文本获取向量维度
            List<Float> testEmbeddings = getEmbeddings("test");
            return testEmbeddings.size();
        } catch (Exception e) {
            log.error("Error getting embedding dimensions", e);
            return 1536; // 默认维度
        }
    }
} 