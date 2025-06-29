package com.mem0.core.service;

import com.mem0.core.entity.Agent;

import java.util.List;

/**
 * 智能体服务接口
 * 
 * @author changyu496
 */
public interface AgentService {
    
    /**
     * 根据ID查询智能体
     */
    Agent getById(Long id);
    
    /**
     * 根据appId和agentId查询智能体
     */
    Agent getByAppIdAndAgentId(Long appId, String agentId);
    
    /**
     * 根据appId查询所有智能体
     */
    List<Agent> getByAppId(Long appId);
    
    /**
     * 查询所有智能体
     */
    List<Agent> getAll();
    
    /**
     * 创建智能体
     */
    Agent create(Agent agent);
    
    /**
     * 更新智能体
     */
    Agent update(Agent agent);
    
    /**
     * 删除智能体
     */
    void deleteById(Long id);
    
    /**
     * 根据状态查询智能体
     */
    List<Agent> getByStatus(Integer status);
} 