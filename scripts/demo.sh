#!/bin/bash

# Mem0 Java 演示脚本
# 作者: Mem0 Team
# 版本: 1.0.0

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 显示欢迎信息
show_welcome() {
    echo "=========================================="
    echo "        Mem0 Java 演示脚本"
    echo "=========================================="
    echo
    echo "本脚本将演示 Mem0 Java 的完整功能："
    echo "1. 部署 Mem0 Java 服务"
    echo "2. 添加用户记忆"
    echo "3. 搜索相关记忆"
    echo "4. 与 AI 进行对话"
    echo "5. 测试向量搜索"
    echo
}

# 检查环境
check_environment() {
    log_info "检查部署环境..."
    
    if [ ! -f .env ]; then
        log_warning "未找到 .env 文件，将创建默认配置"
        cat > .env << EOF
# Mem0 Java 环境配置
MYSQL_ROOT_PASSWORD=mem0root123
MYSQL_DATABASE=mem0
MYSQL_USER=mem0
MYSQL_PASSWORD=mem0pass123
ES_JAVA_OPTS=-Xms1g -Xmx1g
DASHSCOPE_API_KEY=your-dashscope-api-key-here
SERVER_PORT=8080
LOGGING_LEVEL_COM_MEM0=INFO
LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_AI=INFO
EOF
        log_warning "请编辑 .env 文件，设置您的 DashScope API Key"
        read -p "按回车键继续..."
    fi
    
    log_success "环境检查完成"
}

# 部署服务
deploy_services() {
    log_info "开始部署 Mem0 Java 服务..."
    
    if [ -f docker-compose.yml ]; then
        ./scripts/deploy.sh
        log_success "服务部署完成"
    else
        log_error "未找到 docker-compose.yml 文件"
        exit 1
    fi
}

# 等待服务就绪
wait_for_services() {
    log_info "等待服务就绪..."
    
    # 等待应用启动
    timeout=120
    while [ $timeout -gt 0 ]; do
        if curl -f http://localhost:8080/test/health >/dev/null 2>&1; then
            log_success "Mem0 Java 服务已就绪"
            break
        fi
        sleep 3
        timeout=$((timeout - 3))
    done
    
    if [ $timeout -le 0 ]; then
        log_error "服务启动超时"
        exit 1
    fi
}

# 演示添加记忆
demo_add_memory() {
    log_info "演示：添加用户记忆..."
    
    # 添加第一条记忆
    log_info "添加第一条记忆：用户喜欢苹果"
    response1=$(curl -s -X POST http://localhost:8080/test/memory/add \
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
        }')
    echo "响应: $response1"
    
    # 添加第二条记忆
    log_info "添加第二条记忆：用户是程序员"
    response2=$(curl -s -X POST http://localhost:8080/test/memory/add \
        -H "Content-Type: application/json" \
        -d '{
            "userId": "10001",
            "agentId": "3",
            "appId": "3",
            "messages": [
                {
                    "role": "user",
                    "content": "我是一名 Java 程序员，喜欢编程"
                }
            ]
        }')
    echo "响应: $response2"
    
    # 添加第三条记忆
    log_info "添加第三条记忆：用户喜欢运动"
    response3=$(curl -s -X POST http://localhost:8080/test/memory/add \
        -H "Content-Type: application/json" \
        -d '{
            "userId": "10001",
            "agentId": "3",
            "appId": "3",
            "messages": [
                {
                    "role": "user",
                    "content": "我喜欢跑步和游泳"
                }
            ]
        }')
    echo "响应: $response3"
    
    log_success "记忆添加完成"
}

# 演示搜索记忆
demo_search_memory() {
    log_info "演示：搜索用户记忆..."
    
    # 搜索所有记忆
    log_info "搜索用户 10001 的所有记忆"
    response=$(curl -s "http://localhost:8080/test/memory/search?userId=10001")
    echo "搜索结果: $response"
    
    # 分页查询
    log_info "分页查询记忆（第1页，每页2条）"
    response=$(curl -s "http://localhost:8080/test/memory/page?page=1&size=2&userId=10001")
    echo "分页结果: $response"
    
    # 统计记忆数量
    log_info "统计用户记忆数量"
    response=$(curl -s "http://localhost:8080/test/memory/count?userId=10001")
    echo "记忆数量: $response"
    
    log_success "记忆搜索演示完成"
}

# 演示 AI 对话
demo_ai_chat() {
    log_info "演示：AI 对话..."
    
    # 简单对话
    log_info "与 AI 进行简单对话"
    response=$(curl -s -X POST http://localhost:8080/test/chat \
        -H "Content-Type: application/json" \
        -d '{"message": "你好，请介绍一下你自己"}')
    echo "AI 回复: $response"
    
    # 基于记忆的对话
    log_info "基于记忆的对话：询问用户喜好"
    response=$(curl -s -X POST http://localhost:8080/test/chat \
        -H "Content-Type: application/json" \
        -d '{"message": "请告诉我用户 10001 喜欢什么"}')
    echo "AI 回复: $response"
    
    log_success "AI 对话演示完成"
}

# 演示向量搜索
demo_vector_search() {
    log_info "演示：向量搜索..."
    
    # 文本嵌入
    log_info "对文本进行向量嵌入"
    response=$(curl -s -X POST http://localhost:8080/test/embed \
        -H "Content-Type: application/json" \
        -d '{"text": "苹果"}')
    echo "嵌入结果: 向量维度 $(echo "$response" | jq '.embedding | length' 2>/dev/null || echo "未知")"
    
    # 向量搜索
    log_info "基于向量搜索相似内容"
    response=$(curl -s -X POST http://localhost:8080/test/vector/search \
        -H "Content-Type: application/json" \
        -d '{"query": "水果", "limit": 3}')
    echo "向量搜索结果: $response"
    
    log_success "向量搜索演示完成"
}

# 运行 SDK 示例
demo_sdk() {
    log_info "演示：SDK 使用..."
    
    if [ -f mem0-example/pom.xml ]; then
        log_info "运行 SDK 示例程序"
        cd mem0-example
        mvn exec:java -Dexec.mainClass="com.mem0.example.Mem0ExampleApp" || log_warning "SDK 示例运行失败"
        cd ..
    else
        log_warning "未找到 SDK 示例模块"
    fi
    
    log_success "SDK 演示完成"
}

# 显示服务信息
show_service_info() {
    echo
    echo "=========================================="
    echo "           Mem0 Java 演示完成"
    echo "=========================================="
    echo
    echo "服务访问地址:"
    echo "  - Mem0 API:     http://localhost:8080"
    echo "  - 健康检查:     http://localhost:8080/test/health"
    echo "  - Kibana:       http://localhost:5601"
    echo "  - Elasticsearch: http://localhost:9200"
    echo
    echo "测试命令:"
    echo "  - 功能测试:     ./scripts/test.sh"
    echo "  - 查看日志:     docker-compose logs -f"
    echo "  - 停止服务:     ./scripts/stop.sh"
    echo
    echo "示例 API 调用:"
    echo "  curl -X GET 'http://localhost:8080/test/health'"
    echo "  curl -X GET 'http://localhost:8080/test/memory/count'"
    echo "  curl -X POST 'http://localhost:8080/test/chat' -H 'Content-Type: application/json' -d '{\"message\": \"你好\"}'"
    echo
    echo "=========================================="
}

# 主函数
main() {
    show_welcome
    check_environment
    deploy_services
    wait_for_services
    
    # 执行演示
    demo_add_memory
    echo
    demo_search_memory
    echo
    demo_ai_chat
    echo
    demo_vector_search
    echo
    demo_sdk
    echo
    
    show_service_info
    
    log_success "演示完成！"
}

# 执行主函数
main "$@" 