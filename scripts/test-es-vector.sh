#!/bin/bash

# Elasticsearch 向量存储测试脚本

ES_HOST="localhost"
ES_PORT="9200"
ES_URL="http://${ES_HOST}:${ES_PORT}"
INDEX_NAME="mem0_vectors"

echo "测试 Elasticsearch 向量存储功能..."

# 测试1: 存储一个向量
echo "测试1: 存储向量..."
curl -X POST "${ES_URL}/${INDEX_NAME}/_doc/test-001" -H "Content-Type: application/json" -d '{
  "vector": [0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0],
  "content": "这是一个测试记忆",
  "memory_id": "test-001",
  "app_id": 1,
  "agent_id": 1,
  "user_id": 1,
  "memory_type": "fact",
  "version": 1,
  "created_at": "2024-01-01T00:00:00Z",
  "updated_at": "2024-01-01T00:00:00Z",
  "metadata": {
    "source": "test",
    "run_id": "test-run-001"
  }
}'

echo ""
echo ""

# 测试2: 查询向量
echo "测试2: 查询向量..."
curl -X GET "${ES_URL}/${INDEX_NAME}/_doc/test-001" -H "Content-Type: application/json"

echo ""
echo ""

# 测试3: 向量相似度搜索（简化版）
echo "测试3: 向量相似度搜索..."
curl -X POST "${ES_URL}/${INDEX_NAME}/_search" -H "Content-Type: application/json" -d '{
  "query": {
    "script_score": {
      "query": {
        "match_all": {}
      },
      "script": {
        "source": "cosineSimilarity(params.query_vector, \"vector\") + 1.0",
        "params": {
          "query_vector": [0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0]
        }
      }
    }
  },
  "size": 5
}'

echo ""
echo ""
echo "测试完成！" 