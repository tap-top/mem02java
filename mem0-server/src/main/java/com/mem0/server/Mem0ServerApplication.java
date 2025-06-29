package com.mem0.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Mem0 Java 版本主应用类
 * 
 * @author changyu496
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.mem0.server", "com.mem0.core"})
public class Mem0ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(Mem0ServerApplication.class, args);
    }
} 