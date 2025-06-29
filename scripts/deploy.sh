#!/bin/bash

# Mem0 Java 一键部署脚本
# 作者: changyu496
# GitHub: https://github.com/changyu496/mem0-java

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 打印带颜色的消息
print_message() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_step() {
    echo -e "${BLUE}[STEP]${NC} $1"
}

# 检查 Docker 是否安装
check_docker() {
    if ! command -v docker &> /dev/null; then
        print_error "Docker 未安装，请先安装 Docker"
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose 未安装，请先安装 Docker Compose"
        exit 1
    fi
    
    print_message "Docker 环境检查通过"
}

# 检查环境变量文件
check_env_file() {
    if [ ! -f ".env" ]; then
        print_warning "未找到 .env 文件，正在创建..."
        
        if [ -f "env.example" ]; then
            cp env.example .env
            print_warning "已复制 env.example 到 .env"
            print_warning "请编辑 .env 文件，设置您的 DashScope API Key"
            print_warning "获取 API Key: https://dashscope.console.aliyun.com/apiKey"
            exit 1
        else
            print_error "未找到 env.example 文件"
            exit 1
        fi
    fi
    
    # 检查 API Key 是否已设置
    if grep -q "your-dashscope-api-key-here" .env; then
        print_error "请在 .env 文件中设置您的 DashScope API Key"
        print_error "获取地址: https://dashscope.console.aliyun.com/apiKey"
        exit 1
    fi
    
    print_message "环境变量文件检查通过"
}

# 检查端口占用
check_ports() {
    local ports=("8080" "9200" "5601" "3306")
    
    for port in "${ports[@]}"; do
        if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
            print_warning "端口 $port 已被占用"
            read -p "是否停止占用端口 $port 的进程? (y/n): " -n 1 -r
            echo
            if [[ $REPLY =~ ^[Yy]$ ]]; then
                local pid=$(lsof -ti :$port)
                if [ ! -z "$pid" ]; then
                    kill -9 $pid
                    print_message "已停止端口 $port 的进程"
                fi
            else
                print_error "端口 $port 被占用，无法启动服务"
                exit 1
            fi
        fi
    done
}

# 创建必要的目录
create_directories() {
    print_step "创建必要的目录..."
    
    mkdir -p logs
    mkdir -p data/elasticsearch
    mkdir -p data/mysql
    
    print_message "目录创建完成"
}

# 停止现有服务
stop_existing_services() {
    print_step "停止现有服务..."
    
    if docker-compose ps | grep -q "Up"; then
        docker-compose down
        print_message "已停止现有服务"
    fi
}

# 启动服务
start_services() {
    print_step "启动 Mem0 Java 服务..."
    
    # 启动 MySQL 和 Elasticsearch
    docker-compose up -d mysql elasticsearch kibana
    
    print_message "等待 MySQL 启动..."
    sleep 30
    
    print_message "等待 Elasticsearch 启动..."
    sleep 30
    
    # 初始化数据库
    print_step "初始化数据库..."
    docker-compose exec -T mysql mysql -u root -pmem0pass123 < scripts/init-mysql.sql
    
    # 启动应用
    print_step "启动 Mem0 应用..."
    docker-compose up -d mem0-app
    
    print_message "等待应用启动..."
    sleep 30
}

# 健康检查
health_check() {
    print_step "执行健康检查..."
    
    # 检查应用健康状态
    if curl -f http://localhost:8080/test/health > /dev/null 2>&1; then
        print_message "✅ Mem0 应用健康检查通过"
    else
        print_error "❌ Mem0 应用健康检查失败"
        return 1
    fi
    
    # 检查 Elasticsearch
    if curl -f http://localhost:9200/_cluster/health > /dev/null 2>&1; then
        print_message "✅ Elasticsearch 健康检查通过"
    else
        print_error "❌ Elasticsearch 健康检查失败"
        return 1
    fi
    
    # 检查 MySQL
    if docker-compose exec mysql mysqladmin ping -h localhost -u root -pmem0pass123 > /dev/null 2>&1; then
        print_message "✅ MySQL 健康检查通过"
    else
        print_error "❌ MySQL 健康检查失败"
        return 1
    fi
    
    return 0
}

# 显示服务信息
show_service_info() {
    echo
    print_message "🎉 Mem0 Java 部署完成！"
    echo
    echo "📋 服务访问地址:"
    echo "   • Mem0 API: http://localhost:8080"
    echo "   • 健康检查: http://localhost:8080/test/health"
    echo "   • Kibana: http://localhost:5601"
    echo "   • Elasticsearch: http://localhost:9200"
    echo
    echo "🔧 常用命令:"
    echo "   • 查看日志: docker-compose logs -f"
    echo "   • 停止服务: ./scripts/stop.sh"
    echo "   • 重启服务: docker-compose restart"
    echo "   • 查看状态: docker-compose ps"
    echo
    echo "📖 测试命令:"
    echo "   • 运行测试: ./scripts/test.sh"
    echo "   • 演示功能: ./scripts/demo.sh"
    echo
    echo "⚠️  安全提醒:"
    echo "   • 请确保 .env 文件中的 API Key 已正确设置"
    echo "   • 生产环境请修改默认密码"
    echo "   • 定期备份数据库数据"
    echo
}

# 主函数
main() {
    echo "🚀 Mem0 Java 一键部署脚本"
    echo "作者: changyu496"
    echo "GitHub: https://github.com/changyu496/mem0-java"
    echo
    
    check_docker
    check_env_file
    check_ports
    create_directories
    stop_existing_services
    start_services
    
    if health_check; then
        show_service_info
    else
        print_error "部署失败，请检查日志: docker-compose logs"
        exit 1
    fi
}

# 执行主函数
main "$@" 