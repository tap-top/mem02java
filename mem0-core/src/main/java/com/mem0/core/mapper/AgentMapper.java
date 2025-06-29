package com.mem0.core.mapper;

import com.mem0.core.entity.Agent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 智能体数据访问层
 * 
 * @author changyu496
 */
@Mapper
public interface AgentMapper {
    
    /**
     * 根据ID查询智能体
     */
    Agent selectById(@Param("id") Long id);
    
    /**
     * 根据appId和agentId查询智能体
     */
    Agent selectByAppIdAndAgentId(@Param("appId") Long appId, @Param("agentId") String agentId);
    
    /**
     * 根据appId查询所有智能体
     */
    List<Agent> selectByAppId(@Param("appId") Long appId);
    
    /**
     * 查询所有智能体
     */
    List<Agent> selectAll();
    
    /**
     * 插入智能体
     */
    int insert(Agent agent);
    
    /**
     * 更新智能体
     */
    int update(Agent agent);
    
    /**
     * 删除智能体
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据状态查询智能体
     */
    List<Agent> selectByStatus(@Param("status") Integer status);
} 