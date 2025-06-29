package com.mem0.core.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * 数据库配置类
 * 
 * @author changyu496
 */
@Configuration
@MapperScan("com.mem0.core.mapper")
public class DatabaseConfig {
    // MyBatis 配置已在 application.yml 中完成
} 