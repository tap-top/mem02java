package com.mem0.core.vectorstore;

import java.util.List;
import java.util.Map;

/**
 * 向量存储服务接口
 * 
 * @author changyu496
 */
public interface VectorStoreService {
    
    /**
     * 存储向量
     * 
     * @param indexName 索引名称
     * @param id 文档ID
     * @param vector 向量
     * @param metadata 元数据
     */
    void storeVector(String indexName, String id, List<Float> vector, Map<String, Object> metadata);
    
    /**
     * 批量存储向量
     * 
     * @param indexName 索引名称
     * @param vectors 向量数据列表
     */
    void storeVectors(String indexName, List<VectorData> vectors);
    
    /**
     * 向量相似度搜索
     * 
     * @param indexName 索引名称
     * @param queryVector 查询向量
     * @param limit 返回结果数量限制
     * @param filters 过滤条件
     * @return 搜索结果
     */
    List<SearchResult> search(String indexName, List<Float> queryVector, int limit, Map<String, Object> filters);
    
    /**
     * 删除向量
     * 
     * @param indexName 索引名称
     * @param id 文档ID
     */
    void deleteVector(String indexName, String id);
    
    /**
     * 创建索引
     * 
     * @param indexName 索引名称
     * @param dimension 向量维度
     */
    void createIndex(String indexName, int dimension);
    
    /**
     * 删除索引
     * 
     * @param indexName 索引名称
     */
    void deleteIndex(String indexName);
    
    /**
     * 检查索引是否存在
     * 
     * @param indexName 索引名称
     * @return 是否存在
     */
    boolean indexExists(String indexName);
    
    /**
     * 向量数据类
     */
    class VectorData {
        private String id;
        private List<Float> vector;
        private Map<String, Object> metadata;
        
        public VectorData(String id, List<Float> vector, Map<String, Object> metadata) {
            this.id = id;
            this.vector = vector;
            this.metadata = metadata;
        }
        
        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public List<Float> getVector() { return vector; }
        public void setVector(List<Float> vector) { this.vector = vector; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
    
    /**
     * 搜索结果类
     */
    class SearchResult {
        private String id;
        private double score;
        private Map<String, Object> metadata;
        
        public SearchResult(String id, double score, Map<String, Object> metadata) {
            this.id = id;
            this.score = score;
            this.metadata = metadata;
        }
        
        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public double getScore() { return score; }
        public void setScore(double score) { this.score = score; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
} 