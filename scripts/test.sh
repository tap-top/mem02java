#!/bin/bash

# Mem0 Java 测试脚本
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

# 测试健康检查
test_health() {
    log_info "测试健康检查接口..."
    
    response=$(curl -s -w "%{http_code}" http://localhost:8080/test/health)
    http_code="${response: -3}"
    body="${response%???}"
    
    if [ "$http_code" = "200" ]; then
        log_success "健康检查通过: $body"
        return 0
    else
        log_error "健康检查失败: HTTP $http_code"
        return 1
    fi
}

# 测试记忆统计
test_memory_count() {
    log_info "测试记忆统计接口..."
    
    response=$(curl -s -w "%{http_code}" http://localhost:8080/test/memory/count)
    http_code="${response: -3}"
    body="${response%???}"
    
    if [ "$http_code" = "200" ]; then
        log_success "记忆统计通过: $body"
        return 0
    else
        log_error "记忆统计失败: HTTP $http_code"
        return 1
    fi
}

# 测试记忆查询
test_memory_search() {
    log_info "测试记忆查询接口..."
    
    response=$(curl -s -w "%{http_code}" "http://localhost:8080/test/memory/search?userId=10001")
    http_code="${response: -3}"
    body="${response%???}"
    
    if [ "$http_code" = "200" ]; then
        log_success "记忆查询通过: 返回 $(echo "$body" | jq length 2>/dev/null || echo "未知数量") 条记录"
        return 0
    else
        log_error "记忆查询失败: HTTP $http_code"
        return 1
    fi
}

# 测试记忆分页
test_memory_page() {
    log_info "测试记忆分页接口..."
    
    response=$(curl -s -w "%{http_code}" "http://localhost:8080/test/memory/page?page=1&size=5")
    http_code="${response: -3}"
    body="${response%???}"
    
    if [ "$http_code" = "200" ]; then
        log_success "记忆分页通过: $body"
        return 0
    else
        log_error "记忆分页失败: HTTP $http_code"
        return 1
    fi
}

# 测试聊天接口
test_chat() {
    log_info "测试聊天接口..."
    
    response=$(curl -s -w "%{http_code}" -X POST http://localhost:8080/test/chat \
        -H "Content-Type: application/json" \
        -d '{"message": "你好，请介绍一下你自己"}')
    http_code="${response: -3}"
    body="${response%???}"
    
    if [ "$http_code" = "200" ]; then
        log_success "聊天接口通过: $body"
        return 0
    else
        log_error "聊天接口失败: HTTP $http_code"
        return 1
    fi
}

# 测试嵌入接口
test_embed() {
    log_info "测试嵌入接口..."
    
    response=$(curl -s -w "%{http_code}" -X POST http://localhost:8080/test/embed \
        -H "Content-Type: application/json" \
        -d '{"text": "这是一个测试文本"}')
    http_code="${response: -3}"
    body="${response%???}"
    
    if [ "$http_code" = "200" ]; then
        log_success "嵌入接口通过: 向量维度 $(echo "$body" | jq '.embedding | length' 2>/dev/null || echo "未知")"
        return 0
    else
        log_error "嵌入接口失败: HTTP $http_code"
        return 1
    fi
}

# 测试向量搜索
test_vector_search() {
    log_info "测试向量搜索接口..."
    
    response=$(curl -s -w "%{http_code}" -X POST http://localhost:8080/test/vector/search \
        -H "Content-Type: application/json" \
        -d '{"query": "苹果", "limit": 5}')
    http_code="${response: -3}"
    body="${response%???}"
    
    if [ "$http_code" = "200" ]; then
        log_success "向量搜索通过: 返回 $(echo "$body" | jq '.results | length' 2>/dev/null || echo "未知数量") 条结果"
        return 0
    else
        log_error "向量搜索失败: HTTP $http_code"
        return 1
    fi
}

# 检查服务状态
check_services() {
    log_info "检查服务状态..."
    
    # 检查 Docker 容器
    if command -v docker-compose &> /dev/null; then
        if docker-compose ps | grep -q "Up"; then
            log_success "Docker 服务运行正常"
        else
            log_error "Docker 服务未运行"
            return 1
        fi
    fi
    
    # 检查端口
    local ports=("8080" "3306" "9200")
    for port in "${ports[@]}"; do
        if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
            log_success "端口 $port 监听正常"
        else
            log_warning "端口 $port 未监听"
        fi
    done
}

# 主测试函数
main() {
    echo "=========================================="
    echo "        Mem0 Java 功能测试脚本"
    echo "=========================================="
    echo
    
    local total_tests=0
    local passed_tests=0
    
    # 检查服务状态
    check_services
    echo
    
    # 执行测试
    tests=(
        "test_health"
        "test_memory_count"
        "test_memory_search"
        "test_memory_page"
        "test_chat"
        "test_embed"
        "test_vector_search"
    )
    
    for test in "${tests[@]}"; do
        total_tests=$((total_tests + 1))
        if $test; then
            passed_tests=$((passed_tests + 1))
        fi
        echo
    done
    
    # 显示测试结果
    echo "=========================================="
    echo "              测试结果"
    echo "=========================================="
    echo "总测试数: $total_tests"
    echo "通过测试: $passed_tests"
    echo "失败测试: $((total_tests - passed_tests))"
    echo "成功率: $((passed_tests * 100 / total_tests))%"
    echo "=========================================="
    
    if [ $passed_tests -eq $total_tests ]; then
        log_success "所有测试通过！Mem0 Java 部署成功！"
        exit 0
    else
        log_error "部分测试失败，请检查服务状态"
        exit 1
    fi
}

# 执行主函数
main "$@" 