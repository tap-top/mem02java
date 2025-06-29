package com.mem0.core.service.impl;

import com.mem0.core.entity.Agent;
import com.mem0.core.mapper.AgentMapper;
import com.mem0.core.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 智能体服务实现类
 * 
 * @author changyu496
 */
@Service
@Transactional
public class AgentServiceImpl implements AgentService {
    
    @Autowired
    private AgentMapper agentMapper;
    
    @Override
    public Agent getById(Long id) {
        return agentMapper.selectById(id);
    }
    
    @Override
    public Agent getByAppIdAndAgentId(Long appId, String agentId) {
        return agentMapper.selectByAppIdAndAgentId(appId, agentId);
    }
    
    @Override
    public List<Agent> getByAppId(Long appId) {
        return agentMapper.selectByAppId(appId);
    }
    
    @Override
    public List<Agent> getAll() {
        return agentMapper.selectAll();
    }
    
    @Override
    public Agent create(Agent agent) {
        agentMapper.insert(agent);
        return agent;
    }
    
    @Override
    public Agent update(Agent agent) {
        agentMapper.update(agent);
        return agent;
    }
    
    @Override
    public void deleteById(Long id) {
        agentMapper.deleteById(id);
    }
    
    @Override
    public List<Agent> getByStatus(Integer status) {
        return agentMapper.selectByStatus(status);
    }
} 