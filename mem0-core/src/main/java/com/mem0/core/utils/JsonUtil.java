package com.mem0.core.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * JSON工具类
 * 
 * @author changyu496
 */
@Slf4j
public class JsonUtil {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 对象转JSON字符串
     * 
     * @param obj 对象
     * @return JSON字符串
     */
    public static String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Error converting object to JSON: {}", obj, e);
            return null;
        }
    }
    
    /**
     * JSON字符串转对象
     * 
     * @param json JSON字符串
     * @param clazz 目标类型
     * @param <T> 泛型
     * @return 对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("Error converting JSON to object: {}", json, e);
            return null;
        }
    }
    
    /**
     * JSON字符串转Map
     * 
     * @param json JSON字符串
     * @return Map对象
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> fromJsonToMap(String json) {
        try {
            MapType mapType = objectMapper.getTypeFactory().constructMapType(
                Map.class, String.class, Object.class);
            return objectMapper.readValue(json, mapType);
        } catch (JsonProcessingException e) {
            log.error("Error converting JSON to Map: {}", json, e);
            return null;
        }
    }
    
    /**
     * Map转JSON字符串
     * 
     * @param map Map对象
     * @return JSON字符串
     */
    public static String mapToJson(Map<String, Object> map) {
        return toJson(map);
    }
} 