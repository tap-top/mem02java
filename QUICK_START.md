# Mem0 Java 快速开始指南

## 🚀 一键部署

### 前置要求
- Docker 20.10+
- Docker Compose 2.0+
- 4GB+ 可用内存

### 部署步骤

1. **克隆项目**
   ```bash
   git clone https://github.com/your-repo/mem0-java.git
   cd mem0-java
   ```

2. **配置 API Key**
   ```bash
   # 编辑 .env 文件
   vim .env
   # 将 DASHSCOPE_API_KEY=your-dashscope-api-key-here 替换为您的实际 API Key
   ```

3. **一键部署**
   ```bash
   ./scripts/deploy.sh
   ```

4. **验证部署**
   ```bash
   ./scripts/test.sh
   ```

## 🎯 功能演示

运行完整演示：
```bash
./scripts/demo.sh
```

演示内容包括：
- 添加用户记忆
- 搜索相关记忆
- AI 对话
- 向量搜索
- SDK 使用

## 📊 服务访问

| 服务 | 地址 | 说明 |
|------|------|------|
| Mem0 API | http://localhost:8080 | 主应用服务 |
| 健康检查 | http://localhost:8080/test/health | 服务状态 |
| Kibana | http://localhost:5601 | ES 管理界面 |
| Elasticsearch | http://localhost:9200 | 向量存储 |

## 🔧 常用命令

```bash
# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f mem0-app

# 停止服务
./scripts/stop.sh

# 重新部署
docker-compose down && ./scripts/deploy.sh

# 功能测试
./scripts/test.sh
```

## 📝 API 示例

### 健康检查
```bash
curl http://localhost:8080/test/health
```

### 添加记忆
```bash
curl -X POST http://localhost:8080/test/memory/add \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "10001",
    "agentId": "3",
    "appId": "3",
    "messages": [
      {
        "role": "user",
        "content": "我喜欢吃苹果"
      }
    ]
  }'
```

### 搜索记忆
```bash
curl "http://localhost:8080/test/memory/search?userId=10001"
```

### AI 对话
```bash
curl -X POST http://localhost:8080/test/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "你好，请介绍一下你自己"}'
```

## 🛠️ 故障排除

### 常见问题

1. **端口占用**
   ```bash
   lsof -i :8080
   kill -9 <PID>
   ```

2. **服务启动失败**
   ```bash
   docker-compose logs mem0-app
   ```

3. **API Key 错误**
   ```bash
   # 检查配置
   cat .env | grep DASHSCOPE_API_KEY
   
   # 重新启动
   docker-compose restart mem0-app
   ```

### 获取帮助

- 查看详细文档: [README.md](README.md)
- 部署指南: [DEPLOYMENT.md](DEPLOYMENT.md)
- 提交问题: [Issues](https://github.com/your-repo/mem0-java/issues)

---

**注意**: 首次使用请确保已获取有效的 DashScope API Key。 