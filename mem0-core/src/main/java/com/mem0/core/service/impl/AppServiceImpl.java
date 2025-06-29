package com.mem0.core.service.impl;

import com.mem0.core.entity.App;
import com.mem0.core.mapper.AppMapper;
import com.mem0.core.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 应用服务实现类
 * 
 * @author changyu496
 */
@Service
@Transactional
public class AppServiceImpl implements AppService {
    
    @Autowired
    private AppMapper appMapper;
    
    @Override
    public App getById(Long id) {
        return appMapper.selectById(id);
    }
    
    @Override
    public App getByAppKey(String appKey) {
        return appMapper.selectByAppKey(appKey);
    }
    
    @Override
    public List<App> getAll() {
        return appMapper.selectAll();
    }
    
    @Override
    public App create(App app) {
        appMapper.insert(app);
        return app;
    }
    
    @Override
    public App update(App app) {
        appMapper.update(app);
        return app;
    }
    
    @Override
    public void deleteById(Long id) {
        appMapper.deleteById(id);
    }
    
    @Override
    public List<App> getByStatus(Integer status) {
        return appMapper.selectByStatus(status);
    }
} 