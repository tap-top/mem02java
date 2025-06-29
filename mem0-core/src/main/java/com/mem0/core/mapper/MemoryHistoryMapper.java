package com.mem0.core.mapper;

import com.mem0.core.entity.MemoryHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 记忆历史数据访问层
 * 
 * @author changyu496
 */
@Mapper
public interface MemoryHistoryMapper {
    
    /**
     * 根据ID查询记忆历史
     */
    MemoryHistory selectById(@Param("id") Long id);
    
    /**
     * 根据memoryId查询记忆历史列表
     */
    List<MemoryHistory> selectByMemoryId(@Param("memoryId") Long memoryId);
    
    /**
     * 根据memoryId和版本号查询记忆历史
     */
    MemoryHistory selectByMemoryIdAndVersion(@Param("memoryId") Long memoryId, 
                                            @Param("version") Integer version);
    
    /**
     * 插入记忆历史
     */
    int insert(MemoryHistory memoryHistory);
    
    /**
     * 删除记忆历史
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据memoryId删除记忆历史
     */
    int deleteByMemoryId(@Param("memoryId") Long memoryId);
} 