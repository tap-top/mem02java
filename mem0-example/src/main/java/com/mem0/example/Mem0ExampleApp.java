package com.mem0.example;

import com.mem0.sdk.Mem0Client;
import com.mem0.sdk.config.Mem0Config;
import com.mem0.sdk.dto.AddMemoryRequest;
import com.mem0.sdk.dto.MemoryOperationResult;
import com.mem0.sdk.dto.SearchMemoryRequest;
import com.mem0.sdk.dto.SearchMemoryResult;
import com.mem0.sdk.exception.Mem0Exception;

import java.util.Arrays;
import java.util.List;

public class Mem0ExampleApp {
    public static void main(String[] args) {
        try {
            // 配置 SDK
            Mem0Config config = Mem0Config.builder()
                    .serverUrl("http://localhost:8080")
                    .build();
            Mem0Client client = new Mem0Client(config);

            // 添加记忆
            AddMemoryRequest addRequest = AddMemoryRequest.builder()
                    .userId("10001")
                    .agentId("3")
                    .appId("3")
                    .messages(Arrays.asList(
                            AddMemoryRequest.Message.builder()
                                    .role("user")
                                    .content("Mem0 Java SDK 示例测试")
                                    .build()
                    ))
                    .build();
            List<MemoryOperationResult> addResults = client.addMemory(addRequest);
            System.out.println("添加记忆结果: " + addResults);

            // 搜索记忆
            SearchMemoryRequest searchRequest = SearchMemoryRequest.builder()
                    .query("SDK")
                    .userId("10001")
                    .agentId("3")
                    .appId("3")
                    .build();
            SearchMemoryResult searchResult = client.searchMemory(searchRequest);
            System.out.println("搜索记忆结果: " + searchResult);
        } catch (Mem0Exception e) {
            System.err.println("Mem0 SDK 异常: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("其他异常: " + e.getMessage());
        }
    }
} 