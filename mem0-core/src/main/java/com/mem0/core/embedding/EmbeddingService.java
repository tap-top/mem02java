package com.mem0.core.embedding;

import java.util.List;

/**
 * 嵌入向量服务接口
 * 
 * @author changyu496
 */
public interface EmbeddingService {
    
    /**
     * 生成文本嵌入向量
     * 
     * @param text 输入文本
     * @return 嵌入向量
     */
    List<Float> embed(String text);
    
    /**
     * 批量生成文本嵌入向量
     * 
     * @param texts 输入文本列表
     * @return 嵌入向量列表
     */
    List<List<Float>> embedBatch(List<String> texts);
    
    /**
     * 计算两个向量的余弦相似度
     * 
     * @param vector1 向量1
     * @param vector2 向量2
     * @return 相似度分数
     */
    double cosineSimilarity(List<Float> vector1, List<Float> vector2);
    
    /**
     * 计算两个向量的欧几里得距离
     * 
     * @param vector1 向量1
     * @param vector2 向量2
     * @return 距离
     */
    double euclideanDistance(List<Float> vector1, List<Float> vector2);
} 