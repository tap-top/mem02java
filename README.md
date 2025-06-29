# Mem0 Java

> **📢 项目说明**: 这是 [mem0ai/mem0](https://github.com/mem0ai/mem0) 的 **Java 实现版本**，感谢原项目团队提供的优秀创意和架构设计！

> **🙏 致谢**: 本项目基于 [mem0ai/mem0](https://github.com/mem0ai/mem0) 的创意和理念开发，原项目是一个优秀的 AI 记忆管理系统，为 AI 助手和智能体提供智能记忆层。我们在此向原项目团队致以诚挚的感谢！

---

基于 Java 的多租户 AI 记忆系统，使用 Spring AI 和 Qwen 大模型，MySQL + MyBatis 做持久化，Elasticsearch 做向量存储。

## 项目特性

- 🚀 **多租户架构**: 支持多应用、多智能体、多用户的记忆隔离
- 🧠 **智能记忆**: 基于 Qwen 大模型的记忆推理和总结
- 🔍 **向量搜索**: 使用 Elasticsearch 进行语义相似度搜索
- 📊 **分页统计**: 支持记忆的分页查询和统计分析
- 🔧 **SDK 支持**: 提供完整的 Java SDK 供外部集成
- 🐳 **Docker 部署**: 一键部署，开箱即用

## 技术栈

- **后端框架**: Spring Boot 3.2.0
- **AI 框架**: Spring AI + Alibaba DashScope
- **大模型**: Qwen (通义千问)
- **数据库**: MySQL 8.0
- **ORM**: MyBatis
- **向量存储**: Elasticsearch 8.11.0
- **构建工具**: Maven
- **容器化**: Docker + Docker Compose

## 项目结构

```
mem0-java/
├── mem0-core/              # 核心业务模块
│   ├── entity/            # 实体类
│   ├── mapper/            # 数据访问层
│   ├── service/           # 业务服务层
│   ├── vectorstore/       # 向量存储服务
│   └── utils/             # 工具类
├── mem0-server/           # Web 服务模块
│   ├── controller/        # 控制器
│   └── config/           # 配置类
├── mem0-sdk/              # Java SDK 模块
│   ├── client/           # 客户端
│   ├── config/           # 配置类
│   ├── dto/              # 数据传输对象
│   ├── exception/        # 异常类
│   ├── http/             # HTTP 客户端
│   └── service/          # 服务层
├── mem0-example/          # SDK 使用示例
├── scripts/               # 部署脚本
│   ├── deploy.sh         # 一键部署脚本
│   ├── stop.sh           # 停止服务脚本
│   └── init-mysql.sql    # MySQL 初始化脚本
├── Dockerfile            # Docker 镜像构建文件
├── docker-compose.yml    # Docker Compose 配置
└── README.md             # 项目文档
```

## 快速开始

### 方式一：Docker 一键部署（推荐）

#### 前置要求

- Docker 20.10+
- Docker Compose 2.0+
- 4GB+ 可用内存

#### 部署步骤

1. **克隆项目**
   ```bash
   git clone https://github.com/changyu496/mem0-java.git
   cd mem0-java
   ```

2. **配置环境变量**
   ```bash
   # 编辑 .env 文件，设置您的 DashScope API Key
   vim .env
   ```
   
   将 `DASHSCOPE_API_KEY=your-dashscope-api-key-here` 替换为您的实际 API Key

3. **一键部署**
   ```bash
   ./scripts/deploy.sh
   ```

4. **验证部署**
   ```bash
   # 健康检查
   curl http://localhost:8080/test/health
   
   # 查看服务状态
   docker-compose ps
   ```

#### 服务访问地址

- **Mem0 API**: http://localhost:8080
- **健康检查**: http://localhost:8080/test/health
- **Kibana**: http://localhost:5601
- **Elasticsearch**: http://localhost:9200

#### 常用命令

```bash
# 查看日志
docker-compose logs -f

# 停止服务
./scripts/stop.sh

# 重启服务
docker-compose restart

# 查看状态
docker-compose ps
```

### 方式二：本地开发环境

#### 前置要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Elasticsearch 8.11.0+

#### 环境准备

1. **安装 MySQL**
   ```bash
   # macOS
   brew install mysql
   brew services start mysql
   
   # Ubuntu
   sudo apt install mysql-server
   sudo systemctl start mysql
   ```

2. **安装 Elasticsearch**
   ```bash
   # 使用 Docker
   docker run -d --name elasticsearch \
     -p 9200:9200 -p 9300:9300 \
     -e "discovery.type=single-node" \
     -e "xpack.security.enabled=false" \
     elasticsearch:8.11.0
   ```

3. **初始化数据库**
   ```bash
   mysql -u root -p < scripts/init-mysql.sql
   ```

#### 启动应用

1. **编译项目**
   ```bash
   mvn clean install -DskipTests
   ```

2. **配置环境变量**
   ```bash
   export DASHSCOPE_API_KEY=your-dashscope-api-key-here
   ```

3. **启动服务**
   ```bash
   cd mem0-server
   mvn spring-boot:run
   ```

## API 接口

### 基础接口

#### 健康检查
```bash
GET /test/health
```

#### 记忆管理

**添加记忆**
```bash
POST /test/memory/add
Content-Type: application/json

{
  "userId": "10001",
  "agentId": "3",
  "appId": "3",
  "messages": [
    {
      "role": "user",
      "content": "我喜欢吃苹果"
    }
  ]
}
```

**查询记忆**
```bash
GET /test/memory/search?userId=10001&agentId=3&appId=3
```

**分页查询**
```bash
GET /test/memory/page?page=1&size=10&userId=10001
```

**统计记忆**
```bash
GET /test/memory/count?userId=10001
```

#### AI 功能

**聊天对话**
```bash
POST /test/chat
Content-Type: application/json

{
  "message": "你好，请介绍一下你自己"
}
```

**文本嵌入**
```bash
POST /test/embed
Content-Type: application/json

{
  "text": "这是一个测试文本"
}
```

**向量搜索**
```bash
POST /test/vector/search
Content-Type: application/json

{
  "query": "苹果",
  "limit": 10
}
```

## SDK 使用

### Maven 依赖

```xml
<dependency>
    <groupId>com.mem0</groupId>
    <artifactId>mem0-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 基本使用

```java
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

// 搜索记忆
SearchMemoryRequest searchRequest = SearchMemoryRequest.builder()
    .query("SDK")
    .userId("10001")
    .agentId("3")
    .appId("3")
    .build();
SearchMemoryResult searchResult = client.searchMemory(searchRequest);
```

## 配置说明

### 环境变量

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| `DASHSCOPE_API_KEY` | DashScope API Key | 必填 |
| `SPRING_DATASOURCE_URL` | 数据库连接 URL | jdbc:mysql://localhost:3306/mem0 |
| `SPRING_DATASOURCE_USERNAME` | 数据库用户名 | mem0 |
| `SPRING_DATASOURCE_PASSWORD` | 数据库密码 | mem0pass123 |
| `ELASTICSEARCH_HOST` | Elasticsearch 主机 | localhost |
| `ELASTICSEARCH_PORT` | Elasticsearch 端口 | 9200 |

### 数据库配置

项目使用 MySQL 作为主数据库，主要包含以下表：

- `app`: 应用表
- `agent`: 智能体表
- `memory`: 记忆表
- `prompt`: 提示词表

### Elasticsearch 配置

项目使用 Elasticsearch 作为向量存储，索引结构：

- 索引名: `mem0_memories`
- 向量维度: 1536
- 支持字段: content, embedding, metadata, app_id, agent_id, user_id 等

## 开发指南

### 项目编译

```bash
# 编译所有模块
mvn clean install

# 跳过测试编译
mvn clean install -DskipTests

# 编译单个模块
cd mem0-core && mvn clean compile
```

### 运行测试

```bash
# 运行所有测试
mvn test

# 运行特定模块测试
cd mem0-core && mvn test
```

### 代码规范

- 使用 Java 17 语法特性
- 遵循阿里巴巴 Java 开发手册
- 使用 Lombok 简化代码
- 添加完整的 JavaDoc 注释

## 故障排除

### 常见问题

1. **端口占用**
   ```bash
   # 查看端口占用
   lsof -i :8080
   
   # 停止占用进程
   kill -9 <PID>
   ```

2. **数据库连接失败**
   ```bash
   # 检查 MySQL 服务状态
   brew services list | grep mysql
   
   # 重启 MySQL
   brew services restart mysql
   ```

3. **Elasticsearch 启动失败**
   ```bash
   # 检查 ES 日志
   docker logs mem0-elasticsearch
   
   # 重启 ES
   docker restart mem0-elasticsearch
   ```

4. **API Key 配置错误**
   ```bash
   # 检查环境变量
   echo $DASHSCOPE_API_KEY
   
   # 重新设置
   export DASHSCOPE_API_KEY=your-actual-api-key
   ```

### 日志查看

```bash
# Docker 环境
docker-compose logs -f mem0-app

# 本地环境
tail -f logs/mem0-java.log
```

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 更新日志

查看 [CHANGELOG.md](CHANGELOG.md) 了解详细更新历史。

## 联系方式

- 项目主页: https://github.com/changyu496/mem0-java
- 问题反馈: https://github.com/changyu496/mem0-java/issues
- 作者: [@changyu496](https://github.com/changyu496)

---

**注意**: 使用前请确保已获取有效的 DashScope API Key，并在环境变量中正确配置。 