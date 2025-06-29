#!/bin/bash

# Elasticsearch 索引初始化脚本
# 用于创建 mem0 项目的向量存储索引

ES_HOST="localhost"
ES_PORT="9200"
ES_URL="http://${ES_HOST}:${ES_PORT}"

echo "开始初始化 Elasticsearch 索引..."

# 检查 Elasticsearch 是否运行
echo "检查 Elasticsearch 连接..."
if ! curl -s "${ES_URL}" > /dev/null; then
    echo "错误: 无法连接到 Elasticsearch，请确保服务已启动"
    exit 1
fi

echo "Elasticsearch 连接正常"

# 创建向量存储索引
INDEX_NAME="mem0_vectors"
DIMENSION=1536  # 根据 DashScope text-embedding-v1 模型的维度

echo "创建索引: ${INDEX_NAME}"

# 删除已存在的索引（如果存在）
echo "删除已存在的索引..."
curl -X DELETE "${ES_URL}/${INDEX_NAME}" -H "Content-Type: application/json" 2>/dev/null || true

# 创建新索引
echo "创建新索引..."
curl -X PUT "${ES_URL}/${INDEX_NAME}" -H "Content-Type: application/json" -d '{
  "settings": {
    "index": {
      "number_of_shards": 1,
      "number_of_replicas": 0
    }
  },
  "mappings": {
    "properties": {
      "vector": {
        "type": "dense_vector",
        "dims": '"${DIMENSION}"',
        "index": true,
        "similarity": "cosine"
      },
      "content": {
        "type": "text",
        "analyzer": "standard"
      },
      "memory_id": {
        "type": "keyword"
      },
      "app_id": {
        "type": "long"
      },
      "agent_id": {
        "type": "long"
      },
      "user_id": {
        "type": "long"
      },
      "memory_type": {
        "type": "keyword"
      },
      "version": {
        "type": "integer"
      },
      "created_at": {
        "type": "date"
      },
      "updated_at": {
        "type": "date"
      },
      "metadata": {
        "type": "object",
        "dynamic": true
      }
    }
  }
}'

echo ""
echo "检查索引是否创建成功..."
curl -X GET "${ES_URL}/${INDEX_NAME}" -H "Content-Type: application/json"

echo ""
echo "索引初始化完成！"
echo "索引名称: ${INDEX_NAME}"
echo "向量维度: ${DIMENSION}"
echo "访问地址: ${ES_URL}/${INDEX_NAME}" 