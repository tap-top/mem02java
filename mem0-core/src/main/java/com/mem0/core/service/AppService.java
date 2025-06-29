package com.mem0.core.service;

import com.mem0.core.entity.App;

import java.util.List;

/**
 * 应用服务接口
 * 
 * @author changyu496
 */
public interface AppService {
    
    /**
     * 根据ID查询应用
     */
    App getById(Long id);
    
    /**
     * 根据appKey查询应用
     */
    App getByAppKey(String appKey);
    
    /**
     * 查询所有应用
     */
    List<App> getAll();
    
    /**
     * 创建应用
     */
    App create(App app);
    
    /**
     * 更新应用
     */
    App update(App app);
    
    /**
     * 删除应用
     */
    void deleteById(Long id);
    
    /**
     * 根据状态查询应用
     */
    List<App> getByStatus(Integer status);
} 