package com.mem0.core.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 分页结果
 * 
 * @author changyu496
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    
    /**
     * 数据列表
     */
    private List<T> data;
    
    /**
     * 总记录数
     */
    private long total;
    
    /**
     * 当前页码
     */
    private int page;
    
    /**
     * 每页大小
     */
    private int size;
    
    /**
     * 总页数
     */
    private int totalPages;
    
    /**
     * 是否有下一页
     */
    private boolean hasNext;
    
    /**
     * 是否有上一页
     */
    private boolean hasPrevious;
    
    /**
     * 创建分页结果
     * 
     * @param data 数据列表
     * @param total 总记录数
     * @param page 当前页码
     * @param size 每页大小
     * @return 分页结果
     */
    public static <T> PageResult<T> of(List<T> data, long total, int page, int size) {
        int totalPages = (int) Math.ceil((double) total / size);
        boolean hasNext = page < totalPages;
        boolean hasPrevious = page > 1;
        
        return new PageResult<>(data, total, page, size, totalPages, hasNext, hasPrevious);
    }
    
    /**
     * 创建空的分页结果
     * 
     * @param page 当前页码
     * @param size 每页大小
     * @return 空的分页结果
     */
    public static <T> PageResult<T> empty(int page, int size) {
        return new PageResult<>(List.of(), 0, page, size, 0, false, false);
    }
} 