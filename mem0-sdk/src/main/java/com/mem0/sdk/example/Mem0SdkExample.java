package com.mem0.sdk.example;

import com.mem0.sdk.Mem0Client;
import com.mem0.sdk.config.Mem0Config;
import com.mem0.sdk.dto.*;
import com.mem0.sdk.exception.Mem0Exception;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mem0 SDK 使用示例
 * 
 * @author changyu496
 */
public class Mem0SdkExample {
    
    public static void main(String[] args) {
        try {
            // 1. 创建配置
            Mem0Config config = Mem0Config.builder()
                    .serverUrl("http://localhost:8080")
                    .connectTimeout(5000)
                    .readTimeout(30000)
                    .enableRetry(true)
                    .maxRetries(3)
                    .enableLogging(true)
                    .build();
            
            // 2. 创建客户端
            Mem0Client client = new Mem0Client(config);
            
            // 3. 健康检查
            System.out.println("=== 健康检查 ===");
            boolean isHealthy = client.healthCheck();
            System.out.println("服务健康状态: " + isHealthy);
            
            // 4. 添加记忆（带推理）
            System.out.println("\n=== 添加记忆（带推理） ===");
            AddMemoryRequest addRequest = AddMemoryRequest.builder()
                    .messages(Arrays.asList(
                            AddMemoryRequest.Message.builder()
                                    .role("user")
                                    .content("我喜欢吃苹果")
                                    .build()
                    ))
                    .userId("10001")
                    .agentId("3")
                    .appId("3")
                    .runId("r-001")
                    .metadata(new HashMap<String, Object>() {{
                        put("source", "example");
                        put("category", "food");
                    }})
                    .infer(true)
                    .build();
            
            List<MemoryOperationResult> addResults = client.addMemory(addRequest);
            System.out.println("添加记忆结果:");
            for (MemoryOperationResult result : addResults) {
                System.out.println("  - 事件: " + result.getEvent() + ", 内容: " + result.getMemory());
            }
            
            // 5. 搜索记忆
            System.out.println("\n=== 搜索记忆 ===");
            SearchMemoryRequest searchRequest = SearchMemoryRequest.builder()
                    .query("苹果")
                    .userId("10001")
                    .agentId("3")
                    .appId("3")
                    .limit(10)
                    .build();
            
            SearchMemoryResult searchResult = client.searchMemory(searchRequest);
            System.out.println("搜索到 " + searchResult.getTotal() + " 条记忆:");
            for (MemoryInfo memory : searchResult.getMemories()) {
                System.out.println("  - ID: " + memory.getMemoryId() + ", 内容: " + memory.getContent());
            }
            
            // 6. 获取用户记忆列表
            System.out.println("\n=== 获取用户记忆列表 ===");
            List<MemoryInfo> userMemories = client.getUserMemories("10001", 5);
            System.out.println("用户记忆数量: " + userMemories.size());
            for (MemoryInfo memory : userMemories) {
                System.out.println("  - ID: " + memory.getMemoryId() + ", 内容: " + memory.getContent());
            }
            
            // 7. 分页查询
            System.out.println("\n=== 分页查询 ===");
            PaginationRequest paginationRequest = PaginationRequest.builder()
                    .page(1)
                    .size(5)
                    .userId("10001")
                    .agentId("3")
                    .appId("3")
                    .build();
            
            PageResult<MemoryInfo> pageResult = client.getMemoriesWithPagination(paginationRequest);
            System.out.println("分页结果 - 总数: " + pageResult.getTotal() + 
                             ", 当前页: " + pageResult.getPage() + 
                             ", 每页大小: " + pageResult.getSize() + 
                             ", 总页数: " + pageResult.getTotalPages());
            
            // 8. 统计记忆数量
            System.out.println("\n=== 统计记忆数量 ===");
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("userId", "10001");
            conditions.put("agentId", "3");
            long count = client.countMemories(conditions);
            System.out.println("记忆总数: " + count);
            
            // 9. 添加记忆（不带推理）
            System.out.println("\n=== 添加记忆（不带推理） ===");
            AddMemoryRequest rawRequest = AddMemoryRequest.builder()
                    .messages(Arrays.asList(
                            AddMemoryRequest.Message.builder()
                                    .role("user")
                                    .content("今天天气很好")
                                    .build()
                    ))
                    .userId("10001")
                    .agentId("3")
                    .appId("3")
                    .runId("r-002")
                    .metadata(new HashMap<String, Object>() {{
                        put("source", "example");
                        put("category", "weather");
                    }})
                    .infer(false)
                    .build();
            
            List<MemoryOperationResult> rawResults = client.addRawMemory(rawRequest);
            System.out.println("添加原始记忆结果:");
            for (MemoryOperationResult result : rawResults) {
                System.out.println("  - 事件: " + result.getEvent() + ", 内容: " + result.getMemory());
            }
            
            System.out.println("\n=== SDK 示例执行完成 ===");
            
        } catch (Mem0Exception e) {
            System.err.println("Mem0 SDK 异常: " + e.getMessage());
            System.err.println("错误代码: " + e.getErrorCode());
            System.err.println("HTTP 状态码: " + e.getHttpStatus());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("其他异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 