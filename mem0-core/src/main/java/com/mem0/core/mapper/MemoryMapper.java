package com.mem0.core.mapper;

import com.mem0.core.entity.Memory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 记忆数据访问层
 * 
 * @author changyu496
 */
@Mapper
public interface MemoryMapper {
    
    /**
     * 根据ID查询记忆
     */
    Memory selectById(@Param("id") Long id);
    
    /**
     * 根据memoryId查询记忆
     */
    Memory selectByMemoryId(@Param("memoryId") String memoryId);
    
    /**
     * 根据appId、agentId、userId查询记忆列表
     */
    List<Memory> selectByAppAgentUser(@Param("appId") Long appId, 
                                     @Param("agentId") Long agentId, 
                                     @Param("userId") Long userId);
    
    /**
     * 根据appId、agentId、userId和记忆类型查询记忆列表
     */
    List<Memory> selectByAppAgentUserAndType(@Param("appId") Long appId, 
                                            @Param("agentId") Long agentId, 
                                            @Param("userId") Long userId,
                                            @Param("memoryType") String memoryType);
    
    /**
     * 根据条件查询记忆列表
     */
    List<Memory> selectByCondition(@Param("conditions") Map<String, Object> conditions);
    
    /**
     * 分页查询记忆列表
     */
    List<Memory> selectWithPagination(@Param("conditions") Map<String, Object> conditions,
                                     @Param("offset") int offset,
                                     @Param("limit") int limit);
    
    /**
     * 统计记忆总数
     */
    long countTotal();
    
    /**
     * 根据条件统计记忆数量
     */
    long countByCondition(@Param("conditions") Map<String, Object> conditions);
    
    /**
     * 插入记忆
     */
    int insert(Memory memory);
    
    /**
     * 更新记忆
     */
    int update(Memory memory);
    
    /**
     * 删除记忆
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据memoryId删除记忆
     */
    int deleteByMemoryId(@Param("memoryId") String memoryId);
    
    /**
     * 根据appId、agentId、userId删除记忆
     */
    int deleteByAppAgentUser(@Param("appId") Long appId, 
                            @Param("agentId") Long agentId, 
                            @Param("userId") Long userId);
} 