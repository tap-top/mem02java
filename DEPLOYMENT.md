# Mem0 Java 部署指南

本文档详细介绍了 Mem0 Java 项目的部署方法和配置说明。

## 部署方式

### 1. Docker 一键部署（推荐）

#### 前置要求

- Docker 20.10+
- Docker Compose 2.0+
- 4GB+ 可用内存
- 10GB+ 可用磁盘空间

#### 快速部署

```bash
# 1. 克隆项目
git clone https://github.com/your-repo/mem0-java.git
cd mem0-java

# 2. 配置 API Key
vim .env
# 将 DASHSCOPE_API_KEY=your-dashscope-api-key-here 替换为您的实际 API Key

# 3. 一键部署
./scripts/deploy.sh

# 4. 验证部署
./scripts/test.sh
```

## 服务管理

### 常用命令

```bash
# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f mem0-app

# 停止服务
./scripts/stop.sh

# 重新部署
docker-compose down
./scripts/deploy.sh
```

## 配置说明

### 环境变量

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| `DASHSCOPE_API_KEY` | DashScope API Key | 必填 |
| `MYSQL_ROOT_PASSWORD` | MySQL 根密码 | mem0root123 |
| `MYSQL_DATABASE` | 数据库名 | mem0 |
| `MYSQL_USER` | 数据库用户 | mem0 |
| `MYSQL_PASSWORD` | 数据库密码 | mem0pass123 |

### 端口配置

| 服务 | 端口 | 说明 |
|------|------|------|
| Mem0 API | 8080 | 主应用服务 |
| MySQL | 3306 | 数据库服务 |
| Elasticsearch | 9200 | 向量存储服务 |
| Kibana | 5601 | ES 管理界面 |

## 故障排除

### 常见问题

1. **端口占用**: 使用 `lsof -i :端口号` 查看占用进程
2. **内存不足**: 调整 `ES_JAVA_OPTS` 和 `JAVA_OPTS` 参数
3. **API Key 错误**: 检查 `.env` 文件中的配置
4. **服务启动失败**: 查看 `docker-compose logs` 获取详细错误

### 健康检查

```bash
# 应用健康检查
curl http://localhost:8080/test/health

# 完整功能测试
./scripts/test.sh
```

## 联系支持

如遇到部署问题，请查看日志文件或提交 Issue 到项目仓库。 