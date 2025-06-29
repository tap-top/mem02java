package com.mem0.sdk.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 分页结果
 * 
 * @author changyu496
 * @param <T> 数据类型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    
    /**
     * 数据列表
     */
    private List<T> data;
    
    /**
     * 总数量
     */
    private Long total;
    
    /**
     * 页码
     */
    private Integer page;
    
    /**
     * 每页大小
     */
    private Integer size;
    
    /**
     * 总页数
     */
    private Integer totalPages;
    
    /**
     * 是否有下一页
     */
    private Boolean hasNext;
    
    /**
     * 是否有上一页
     */
    private Boolean hasPrevious;
    
    /**
     * 计算总页数
     * 
     * @return 总页数
     */
    public Integer getTotalPages() {
        if (total == null || size == null || size == 0) {
            return 0;
        }
        return (int) Math.ceil((double) total / size);
    }
    
    /**
     * 检查是否有下一页
     * 
     * @return 是否有下一页
     */
    public Boolean getHasNext() {
        if (page == null || size == null || total == null) {
            return false;
        }
        return page * size < total;
    }
    
    /**
     * 检查是否有上一页
     * 
     * @return 是否有上一页
     */
    public Boolean getHasPrevious() {
        if (page == null) {
            return false;
        }
        return page > 1;
    }
    
    /**
     * 创建空的分页结果
     * 
     * @param <T> 数据类型
     * @return 空的分页结果
     */
    public static <T> PageResult<T> empty() {
        return PageResult.<T>builder()
                .data(List.of())
                .total(0L)
                .page(1)
                .size(20)
                .totalPages(0)
                .hasNext(false)
                .hasPrevious(false)
                .build();
    }
} 