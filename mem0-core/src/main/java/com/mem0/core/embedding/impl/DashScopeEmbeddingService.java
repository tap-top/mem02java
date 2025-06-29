package com.mem0.core.embedding.impl;

import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingOptions;
import com.mem0.core.embedding.EmbeddingService;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 基于DashScope的嵌入向量服务实现
 * 
 * @author changyu496
 */
@Service
public class DashScopeEmbeddingService implements EmbeddingService {
    
    @Autowired
    private EmbeddingModel embeddingModel;
    
    @Override
    public List<Float> embed(String text) {
        try {
            EmbeddingResponse response = embeddingModel.call(new EmbeddingRequest(
                List.of(text),
                DashScopeEmbeddingOptions.builder().withModel("text-embedding-v1").build()
            ));
            float[] embedding = response.getResult().getOutput();
            
            // 转换为List<Float>
            List<Float> result = new ArrayList<>();
            for (float f : embedding) {
                result.add(f);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("生成嵌入向量失败: " + text, e);
        }
    }
    
    @Override
    public List<List<Float>> embedBatch(List<String> texts) {
        List<List<Float>> results = new ArrayList<>();
        for (String text : texts) {
            results.add(embed(text));
        }
        return results;
    }
    
    @Override
    public double cosineSimilarity(List<Float> vector1, List<Float> vector2) {
        if (vector1.size() != vector2.size()) {
            throw new IllegalArgumentException("向量维度不匹配");
        }
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (int i = 0; i < vector1.size(); i++) {
            double v1 = vector1.get(i);
            double v2 = vector2.get(i);
            dotProduct += v1 * v2;
            norm1 += v1 * v1;
            norm2 += v2 * v2;
        }
        
        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
    
    @Override
    public double euclideanDistance(List<Float> vector1, List<Float> vector2) {
        if (vector1.size() != vector2.size()) {
            throw new IllegalArgumentException("向量维度不匹配");
        }
        
        double sum = 0.0;
        for (int i = 0; i < vector1.size(); i++) {
            double diff = vector1.get(i) - vector2.get(i);
            sum += diff * diff;
        }
        
        return Math.sqrt(sum);
    }
} 