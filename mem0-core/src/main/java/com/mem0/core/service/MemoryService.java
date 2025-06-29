package com.mem0.core.service;

import com.mem0.core.dto.PageResult;
import com.mem0.core.entity.Memory;
import com.mem0.core.mapper.MemoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 记忆服务
 * 
 * @author changyu496
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemoryService {
    
    private final MemoryMapper memoryMapper;
    
    /**
     * 创建记忆
     */
    @Transactional
    public Memory createMemory(Memory memory) {
        memoryMapper.insert(memory);
        return memory;
    }
    
    /**
     * 根据ID获取记忆
     */
    public Memory getMemoryById(Long id) {
        return memoryMapper.selectById(id);
    }
    
    /**
     * 更新记忆
     */
    @Transactional
    public void updateMemory(Memory memory) {
        memoryMapper.update(memory);
    }
    
    /**
     * 删除记忆
     */
    @Transactional
    public void deleteMemory(Long id) {
        memoryMapper.deleteById(id);
    }
    
    /**
     * 根据条件查询记忆
     */
    public List<Memory> getMemoriesByConditions(Map<String, Object> conditions) {
        return memoryMapper.selectByCondition(conditions);
    }
    
    /**
     * 根据用户ID查询记忆
     */
    public List<Memory> getMemoriesByUserId(String userId) {
        // 构建条件查询
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("userId", userId);
        return memoryMapper.selectByCondition(conditions);
    }
    
    /**
     * 根据智能体ID查询记忆
     */
    public List<Memory> getMemoriesByAgentId(String agentId) {
        // 构建条件查询
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("agentId", agentId);
        return memoryMapper.selectByCondition(conditions);
    }
    
    /**
     * 根据应用ID查询记忆
     */
    public List<Memory> getMemoriesByAppId(String appId) {
        // 构建条件查询
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("appId", appId);
        return memoryMapper.selectByCondition(conditions);
    }
    
    /**
     * 分页查询记忆
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 记忆列表
     */
    public List<Memory> getMemoriesWithPagination(int offset, int limit) {
        return getMemoriesWithPagination(null, offset, limit);
    }
    
    /**
     * 分页查询记忆（带条件）
     * 
     * @param conditions 查询条件
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 记忆列表
     */
    public List<Memory> getMemoriesWithPagination(Map<String, Object> conditions, int offset, int limit) {
        if (conditions == null) {
            conditions = new HashMap<>();
        }
        
        // 参数验证
        if (offset < 0) {
            offset = 0;
        }
        if (limit <= 0) {
            limit = 20; // 默认每页20条
        }
        if (limit > 1000) {
            limit = 1000; // 最大限制1000条
        }
        
        log.debug("分页查询记忆: conditions={}, offset={}, limit={}", conditions, offset, limit);
        return memoryMapper.selectWithPagination(conditions, offset, limit);
    }
    
    /**
     * 分页查询记忆（返回分页结果）
     * 
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 分页结果
     */
    public PageResult<Memory> getMemoriesPage(int page, int size) {
        return getMemoriesPage(null, page, size);
    }
    
    /**
     * 分页查询记忆（带条件，返回分页结果）
     * 
     * @param conditions 查询条件
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 分页结果
     */
    public PageResult<Memory> getMemoriesPage(Map<String, Object> conditions, int page, int size) {
        // 参数验证
        if (page < 1) {
            page = 1;
        }
        if (size <= 0) {
            size = 20; // 默认每页20条
        }
        if (size > 1000) {
            size = 1000; // 最大限制1000条
        }
        
        // 计算偏移量
        int offset = (page - 1) * size;
        
        // 查询数据
        List<Memory> data = getMemoriesWithPagination(conditions, offset, size);
        
        // 查询总数
        long total = countMemoriesByConditions(conditions);
        
        log.debug("分页查询记忆: conditions={}, page={}, size={}, total={}", conditions, page, size, total);
        return PageResult.of(data, total, page, size);
    }
    
    /**
     * 统计记忆数量
     * 
     * @return 记忆总数
     */
    public long countMemories() {
        log.debug("统计记忆总数");
        return memoryMapper.countTotal();
    }
    
    /**
     * 根据条件统计记忆数量
     * 
     * @param conditions 统计条件
     * @return 记忆数量
     */
    public long countMemoriesByConditions(Map<String, Object> conditions) {
        if (conditions == null) {
            conditions = new HashMap<>();
        }
        
        log.debug("根据条件统计记忆数量: conditions={}", conditions);
        return memoryMapper.countByCondition(conditions);
    }
    
    /**
     * 根据用户ID统计记忆数量
     * 
     * @param userId 用户ID
     * @return 记忆数量
     */
    public long countMemoriesByUserId(String userId) {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("userId", userId);
        return countMemoriesByConditions(conditions);
    }
    
    /**
     * 根据智能体ID统计记忆数量
     * 
     * @param agentId 智能体ID
     * @return 记忆数量
     */
    public long countMemoriesByAgentId(String agentId) {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("agentId", agentId);
        return countMemoriesByConditions(conditions);
    }
    
    /**
     * 根据应用ID统计记忆数量
     * 
     * @param appId 应用ID
     * @return 记忆数量
     */
    public long countMemoriesByAppId(String appId) {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("appId", appId);
        return countMemoriesByConditions(conditions);
    }
} 