# Mem0 Java SDK

Mem0 Java SDK 是一个专为 Java 应用程序设计的记忆管理客户端库，提供简单易用的 API 接口来与 Mem0 记忆系统进行交互。

## 功能特性

- 🧠 **记忆管理**: 添加、搜索、获取、更新、删除记忆
- 🤖 **智能推理**: 支持带推理的记忆添加，自动识别相似记忆
- 📊 **分页查询**: 支持分页查询和统计功能
- 🔍 **向量搜索**: 基于语义相似度的记忆搜索
- 🛡️ **异常处理**: 完善的异常处理机制
- 🔄 **重试机制**: 自动重试和容错处理
- 📝 **日志记录**: 详细的日志记录功能

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.mem0</groupId>
    <artifactId>mem0-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 创建客户端

```java
import com.mem0.sdk.Mem0Client;
import com.mem0.sdk.config.Mem0Config;

// 创建配置
Mem0Config config = Mem0Config.builder()
    .serverUrl("http://localhost:8080")
    .connectTimeout(5000)
    .readTimeout(30000)
    .enableRetry(true)
    .maxRetries(3)
    .build();

// 创建客户端
Mem0Client client = new Mem0Client(config);
```

### 3. 使用示例

#### 添加记忆（带推理）

```java
import com.mem0.sdk.dto.*;

AddMemoryRequest request = AddMemoryRequest.builder()
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
    .metadata(Map.of("source", "example"))
    .infer(true)
    .build();

List<MemoryOperationResult> results = client.addMemory(request);
```

#### 搜索记忆

```java
SearchMemoryRequest request = SearchMemoryRequest.builder()
    .query("苹果")
    .userId("10001")
    .agentId("3")
    .appId("3")
    .limit(10)
    .build();

SearchMemoryResult result = client.searchMemory(request);
```

#### 分页查询

```java
PaginationRequest request = PaginationRequest.builder()
    .page(1)
    .size(20)
    .userId("10001")
    .agentId("3")
    .appId("3")
    .build();

PageResult<MemoryInfo> result = client.getMemoriesWithPagination(request);
```

## API 参考

### Mem0Client

主要的客户端类，提供所有记忆管理功能。

#### 构造函数

```java
Mem0Client(Mem0Config config)
```

#### 主要方法

| 方法 | 描述 |
|------|------|
| `addMemory(AddMemoryRequest)` | 添加记忆（带推理） |
| `addRawMemory(AddMemoryRequest)` | 添加记忆（不带推理） |
| `searchMemory(SearchMemoryRequest)` | 搜索记忆 |
| `getMemory(String)` | 获取单个记忆 |
| `updateMemory(String, UpdateMemoryRequest)` | 更新记忆 |
| `deleteMemory(String)` | 删除记忆 |
| `getUserMemories(String, int)` | 获取用户记忆列表 |
| `getAgentMemories(String, int)` | 获取智能体记忆列表 |
| `getMemoriesWithPagination(PaginationRequest)` | 分页查询记忆 |
| `countMemories(Map<String, Object>)` | 统计记忆数量 |
| `healthCheck()` | 健康检查 |

### Mem0Config

SDK 配置类，用于配置客户端行为。

#### 主要配置项

| 配置项 | 类型 | 默认值 | 描述 |
|--------|------|--------|------|
| `serverUrl` | String | - | 服务器地址（必需） |
| `connectTimeout` | int | 5000 | 连接超时时间（毫秒） |
| `readTimeout` | int | 30000 | 读取超时时间（毫秒） |
| `enableRetry` | boolean | true | 是否启用重试 |
| `maxRetries` | int | 3 | 最大重试次数 |
| `retryInterval` | long | 1000 | 重试间隔（毫秒） |
| `enableLogging` | boolean | true | 是否启用日志 |
| `logLevel` | String | "INFO" | 日志级别 |

#### 预设配置

```java
// 默认配置
Mem0Config.defaultConfig("http://localhost:8080");

// 生产环境配置
Mem0Config.productionConfig("http://localhost:8080");

// 开发环境配置
Mem0Config.developmentConfig("http://localhost:8080");
```

### 数据传输对象 (DTO)

#### AddMemoryRequest

记忆添加请求。

```java
AddMemoryRequest.builder()
    .messages(List<Message>)           // 消息列表
    .userId(String)                    // 用户ID
    .agentId(String)                   // 智能体ID
    .appId(String)                     // 应用ID
    .runId(String)                     // 运行ID
    .metadata(Map<String, Object>)     // 元数据
    .infer(Boolean)                    // 是否推理
    .memoryType(String)                // 记忆类型
    .prompt(String)                    // 自定义提示词
    .build()
```

#### SearchMemoryRequest

记忆搜索请求。

```java
SearchMemoryRequest.builder()
    .query(String)                     // 查询内容
    .userId(String)                    // 用户ID
    .agentId(String)                   // 智能体ID
    .appId(String)                     // 应用ID
    .limit(Integer)                    // 限制数量
    .filters(Map<String, Object>)      // 过滤条件
    .memoryType(String)                // 记忆类型
    .includeVectorSearch(Boolean)      // 是否包含向量搜索
    .similarityThreshold(Double)       // 相似度阈值
    .build()
```

#### MemoryInfo

记忆信息。

```java
MemoryInfo.builder()
    .id(Long)                          // 主键ID
    .appId(Long)                       // 应用ID
    .agentId(Long)                     // 智能体ID
    .userId(Long)                      // 用户ID
    .memoryId(String)                  // 记忆ID
    .memoryType(String)                // 记忆类型
    .content(String)                   // 记忆内容
    .metadata(Map<String, Object>)     // 元数据
    .embeddingId(String)               // 向量ID
    .version(Integer)                  // 版本号
    .createdAt(LocalDateTime)          // 创建时间
    .updatedAt(LocalDateTime)          // 更新时间
    .similarityScore(Double)           // 相似度分数
    .build()
```

#### MemoryOperationResult

记忆操作结果。

```java
MemoryOperationResult.builder()
    .id(String)                        // 记忆ID
    .memory(String)                    // 记忆内容
    .event(String)                     // 操作事件
    .previousMemory(String)            // 之前的记忆内容
    .success(Boolean)                  // 操作是否成功
    .errorMessage(String)              // 错误信息
    .build()
```

## 异常处理

SDK 使用 `Mem0Exception` 来处理各种异常情况。

### 异常类型

| 异常类型 | 错误代码 | HTTP 状态码 | 描述 |
|----------|----------|-------------|------|
| 网络异常 | `NETWORK_ERROR` | 0 | 网络连接问题 |
| 认证异常 | `AUTHENTICATION_ERROR` | 401 | 认证失败 |
| 授权异常 | `AUTHORIZATION_ERROR` | 403 | 权限不足 |
| 参数异常 | `PARAMETER_ERROR` | 400 | 参数错误 |
| 资源不存在 | `NOT_FOUND_ERROR` | 404 | 资源不存在 |
| 服务器异常 | `SERVER_ERROR` | 500 | 服务器错误 |

### 异常处理示例

```java
try {
    List<MemoryOperationResult> results = client.addMemory(request);
    // 处理成功结果
} catch (Mem0Exception e) {
    System.err.println("错误代码: " + e.getErrorCode());
    System.err.println("错误消息: " + e.getMessage());
    System.err.println("HTTP 状态码: " + e.getHttpStatus());
    
    // 根据异常类型进行处理
    switch (e.getErrorCode()) {
        case "NETWORK_ERROR":
            // 处理网络异常
            break;
        case "AUTHENTICATION_ERROR":
            // 处理认证异常
            break;
        case "PARAMETER_ERROR":
            // 处理参数异常
            break;
        default:
            // 处理其他异常
            break;
    }
}
```

## 最佳实践

### 1. 配置管理

```java
// 推荐使用环境变量或配置文件管理服务器地址
String serverUrl = System.getenv("MEM0_SERVER_URL");
if (serverUrl == null) {
    serverUrl = "http://localhost:8080"; // 默认值
}

Mem0Config config = Mem0Config.builder()
    .serverUrl(serverUrl)
    .build();
```

### 2. 客户端复用

```java
// 推荐在应用程序中复用客户端实例
public class Mem0Service {
    private final Mem0Client client;
    
    public Mem0Service(String serverUrl) {
        Mem0Config config = Mem0Config.defaultConfig(serverUrl);
        this.client = new Mem0Client(config);
    }
    
    // 业务方法...
}
```

### 3. 异常处理

```java
// 推荐使用统一的异常处理
public class Mem0ExceptionHandler {
    public static void handle(Mem0Exception e) {
        log.error("Mem0 操作失败: {}", e.getMessage());
        
        // 根据异常类型进行相应处理
        if (e.getHttpStatus() >= 500) {
            // 服务器错误，可以重试
            retryOperation();
        } else if (e.getHttpStatus() == 401) {
            // 认证失败，需要重新认证
            reauthenticate();
        }
    }
}
```

### 4. 批量操作

```java
// 对于批量操作，推荐使用批处理
public void batchAddMemories(List<AddMemoryRequest> requests) {
    for (AddMemoryRequest request : requests) {
        try {
            client.addMemory(request);
        } catch (Mem0Exception e) {
            log.error("批量添加记忆失败: {}", e.getMessage());
            // 继续处理其他请求
        }
    }
}
```

## 日志配置

SDK 使用 SLF4J 进行日志记录，可以通过配置来控制日志级别。

### 日志级别

- `DEBUG`: 详细的调试信息，包括请求和响应内容
- `INFO`: 一般信息
- `WARN`: 警告信息
- `ERROR`: 错误信息

### 配置示例

```properties
# logback.xml 或 log4j2.xml
<logger name="com.mem0.sdk" level="DEBUG"/>
```

## 版本兼容性

- **Java 版本**: 17+
- **Spring Boot**: 3.0+ (如果使用 Spring Boot)
- **Jackson**: 2.13+ (用于 JSON 处理)

## 许可证

本项目采用 MIT 许可证。详见 [LICENSE](../LICENSE) 文件。

## 贡献

欢迎提交 Issue 和 Pull Request！

## 支持

如有问题，请通过以下方式联系：

- 提交 GitHub Issue
- 发送邮件至项目维护者
- 查看项目文档

---

**注意**: 使用 SDK 前请确保 Mem0 服务器已正确部署并运行。 