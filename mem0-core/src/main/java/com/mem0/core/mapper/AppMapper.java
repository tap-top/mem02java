package com.mem0.core.mapper;

import com.mem0.core.entity.App;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 应用数据访问层
 * 
 * @author changyu496
 */
@Mapper
public interface AppMapper {
    
    /**
     * 根据ID查询应用
     */
    App selectById(@Param("id") Long id);
    
    /**
     * 根据appKey查询应用
     */
    App selectByAppKey(@Param("appKey") String appKey);
    
    /**
     * 查询所有应用
     */
    List<App> selectAll();
    
    /**
     * 插入应用
     */
    int insert(App app);
    
    /**
     * 更新应用
     */
    int update(App app);
    
    /**
     * 删除应用
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据状态查询应用
     */
    List<App> selectByStatus(@Param("status") Integer status);
} 