#!/bin/bash

# Mem0 Java 停止服务脚本
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

# 停止服务
stop_services() {
    log_info "停止 Mem0 Java 服务..."
    
    if [ -f docker-compose.yml ]; then
        docker-compose down
        log_success "服务已停止"
    else
        log_error "未找到 docker-compose.yml 文件"
        exit 1
    fi
}

# 清理资源（可选）
cleanup() {
    read -p "是否清理 Docker 资源（镜像、网络）？(y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        log_info "清理 Docker 资源..."
        
        # 删除相关镜像
        docker rmi mem0-java_mem0-app 2>/dev/null || log_warning "未找到应用镜像"
        
        # 删除相关网络
        docker network rm mem0-java_mem0-network 2>/dev/null || log_warning "未找到网络"
        
        log_success "资源清理完成"
    fi
}

# 主函数
main() {
    echo "=========================================="
    echo "        Mem0 Java 停止服务脚本"
    echo "=========================================="
    echo
    
    stop_services
    cleanup
    
    log_success "服务停止完成！"
}

# 执行主函数
main "$@" 