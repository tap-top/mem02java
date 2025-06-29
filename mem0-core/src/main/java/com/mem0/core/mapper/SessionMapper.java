package com.mem0.core.mapper;

import com.mem0.core.entity.Session;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会话数据访问层
 * 
 * @author changyu496
 */
@Mapper
public interface SessionMapper {
    
    /**
     * 根据ID查询会话
     */
    Session selectById(@Param("id") Long id);
    
    /**
     * 根据sessionId查询会话
     */
    Session selectBySessionId(@Param("sessionId") String sessionId);
    
    /**
     * 根据appId、agentId、userId查询会话列表
     */
    List<Session> selectByAppAgentUser(@Param("appId") Long appId, 
                                      @Param("agentId") Long agentId, 
                                      @Param("userId") Long userId);
    
    /**
     * 根据状态查询会话列表
     */
    List<Session> selectByStatus(@Param("status") String status);
    
    /**
     * 插入会话
     */
    int insert(Session session);
    
    /**
     * 更新会话
     */
    int update(Session session);
    
    /**
     * 删除会话
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据sessionId删除会话
     */
    int deleteBySessionId(@Param("sessionId") String sessionId);
} 