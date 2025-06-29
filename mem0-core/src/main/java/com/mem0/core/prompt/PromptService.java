package com.mem0.core.prompt;

import java.util.List;
import java.util.Map;

/**
 * 提示词服务接口 - 对齐原版mem0
 * 
 * @author changyu496
 */
public interface PromptService {
    
    /**
     * 获取事实提取提示词（对齐原版get_fact_retrieval_messages）
     * 
     * @param parsedMessages 解析后的消息内容
     * @return [systemPrompt, userPrompt]
     */
    String[] getFactRetrievalMessages(String parsedMessages);
    
    /**
     * 获取记忆更新提示词（对齐原版get_update_memory_messages）
     * 
     * @param retrievedOldMemory 检索到的旧记忆
     * @param newRetrievedFacts 新提取的事实
     * @param customUpdateMemoryPrompt 自定义更新记忆提示词
     * @return 完整的提示词
     */
    String getUpdateMemoryMessages(List<Map<String, String>> retrievedOldMemory, 
                                  List<String> newRetrievedFacts, 
                                  String customUpdateMemoryPrompt);
    
    /**
     * 生成记忆搜索提示词
     * 
     * @param query 用户查询
     * @param memories 相关记忆列表
     * @return 提示词
     */
    String generateMemorySearchPrompt(String query, List<String> memories);
    
    /**
     * 生成记忆总结提示词
     * 
     * @param memories 记忆列表
     * @return 提示词
     */
    String generateMemorySummaryPrompt(List<String> memories);
    
    /**
     * 生成对话上下文提示词
     * 
     * @param query 用户查询
     * @param context 上下文信息
     * @param memories 相关记忆
     * @return 提示词
     */
    String generateConversationPrompt(String query, Map<String, Object> context, List<String> memories);
} 