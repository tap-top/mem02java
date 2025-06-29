#!/bin/bash

# Mem0 Java 版本启动脚本

echo "正在启动 Mem0 Java 服务..."

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo "错误: 未找到Java环境，请确保已安装JDK 17+"
    exit 1
fi

# 检查Maven环境
if ! command -v mvn &> /dev/null; then
    echo "错误: 未找到Maven环境，请确保已安装Maven 3.6+"
    exit 1
fi

# 检查数据库连接
echo "检查数据库连接..."
mysql -h localhost -u root -p31Eq845F -e "USE mem0;" 2>/dev/null
if [ $? -ne 0 ]; then
    echo "警告: 无法连接到数据库，请确保MySQL服务已启动且数据库已创建"
    echo "可以运行以下命令创建数据库:"
    echo "mysql -u root -p < scripts/init-db.sql"
fi

# 编译项目
echo "编译项目..."
mvn clean compile -q
if [ $? -ne 0 ]; then
    echo "错误: 项目编译失败"
    exit 1
fi

# 启动服务
echo "启动服务..."
cd mem0-server
mvn spring-boot:run -Dspring-boot.run.profiles=dev

echo "Mem0 Java 服务已启动，访问地址: http://localhost:8080" 