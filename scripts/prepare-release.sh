#!/bin/bash

# GitHub 发布准备脚本
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

# 检查 Git 状态
check_git_status() {
    print_step "检查 Git 状态..."
    
    if [ -n "$(git status --porcelain)" ]; then
        print_error "工作目录有未提交的更改，请先提交或暂存"
        git status --short
        exit 1
    fi
    
    print_message "Git 状态检查通过"
}

# 检查当前分支
check_branch() {
    print_step "检查当前分支..."
    
    current_branch=$(git branch --show-current)
    if [ "$current_branch" != "main" ] && [ "$current_branch" != "master" ]; then
        print_warning "当前分支是 $current_branch，建议在 main 分支上发布"
        read -p "是否继续? (y/n): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            exit 0
        fi
    fi
    
    print_message "分支检查通过"
}

# 运行测试
run_tests() {
    print_step "运行测试..."
    
    if mvn test -q; then
        print_message "测试通过"
    else
        print_error "测试失败，请修复后重试"
        exit 1
    fi
}

# 编译项目
build_project() {
    print_step "编译项目..."
    
    if mvn clean install -DskipTests -q; then
        print_message "编译成功"
    else
        print_error "编译失败，请检查错误信息"
        exit 1
    fi
}

# 检查敏感信息
check_secrets() {
    print_step "检查敏感信息..."
    
    # 检查是否包含 API Key
    if grep -r "sk-" . --exclude-dir=target --exclude-dir=.git --exclude=*.log; then
        print_error "发现硬编码的 API Key，请移除后重试"
        exit 1
    fi
    
    # 检查是否包含密码
    if grep -r "password.*=" . --exclude-dir=target --exclude-dir=.git --exclude=*.log | grep -v "password.*\\$"; then
        print_warning "发现可能的硬编码密码，请检查"
    fi
    
    print_message "敏感信息检查通过"
}

# 更新版本号
update_version() {
    print_step "更新版本号..."
    
    read -p "请输入新版本号 (例如: 1.0.1): " new_version
    
    if [[ ! $new_version =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
        print_error "版本号格式不正确，请使用 MAJOR.MINOR.PATCH 格式"
        exit 1
    fi
    
    # 更新根 pom.xml
    mvn versions:set -DnewVersion=$new_version -q
    
    print_message "版本号已更新为 $new_version"
}

# 更新 CHANGELOG
update_changelog() {
    print_step "更新 CHANGELOG..."
    
    read -p "请输入版本描述: " version_description
    
    # 在 CHANGELOG.md 开头添加新版本
    sed -i.bak "3i\\
## [$new_version] - $(date +%Y-%m-%d)\\
\\
### $version_description\\
\\
#### ✨ 新增功能\\
- \\
\\
#### 🔧 技术改进\\
- \\
\\
#### 🐛 Bug 修复\\
- \\
\\
#### 📚 文档更新\\
- \\
\\
" CHANGELOG.md
    
    print_message "CHANGELOG 已更新"
}

# 提交更改
commit_changes() {
    print_step "提交更改..."
    
    git add .
    git commit -m "chore: 发布版本 $new_version"
    
    print_message "更改已提交"
}

# 创建标签
create_tag() {
    print_step "创建 Git 标签..."
    
    git tag -a "v$new_version" -m "Release version $new_version"
    
    print_message "标签 v$new_version 已创建"
}

# 推送到远程
push_to_remote() {
    print_step "推送到远程仓库..."
    
    git push origin main
    git push origin "v$new_version"
    
    print_message "代码和标签已推送到远程"
}

# 显示发布信息
show_release_info() {
    echo
    print_message "🎉 发布准备完成！"
    echo
    echo "📋 发布信息:"
    echo "   • 版本号: $new_version"
    echo "   • 标签: v$new_version"
    echo "   • 分支: $(git branch --show-current)"
    echo
    echo "🔗 GitHub 操作:"
    echo "   1. 访问: https://github.com/changyu496/mem0-java/releases"
    echo "   2. 点击 'Create a new release'"
    echo "   3. 选择标签: v$new_version"
    echo "   4. 填写发布说明"
    echo "   5. 点击 'Publish release'"
    echo
    echo "📦 发布包:"
    echo "   • 源码: https://github.com/changyu496/mem0-java/archive/v$new_version.tar.gz"
    echo "   • ZIP: https://github.com/changyu496/mem0-java/archive/v$new_version.zip"
    echo
}

# 主函数
main() {
    echo "🚀 Mem0 Java GitHub 发布准备脚本"
    echo "作者: changyu496"
    echo "GitHub: https://github.com/changyu496/mem0-java"
    echo
    
    check_git_status
    check_branch
    check_secrets
    run_tests
    build_project
    update_version
    update_changelog
    commit_changes
    create_tag
    push_to_remote
    show_release_info
}

# 执行主函数
main "$@" 