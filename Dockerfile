# 使用 OpenJDK 17 作为基础镜像
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 设置环境变量
ENV JAVA_OPTS="-Xmx2g -Xms1g"
ENV SPRING_PROFILES_ACTIVE="docker"

# 复制 Maven 配置文件
COPY pom.xml ./
COPY mem0-core/pom.xml ./mem0-core/
COPY mem0-server/pom.xml ./mem0-server/
COPY mem0-sdk/pom.xml ./mem0-sdk/
COPY mem0-example/pom.xml ./mem0-example/

# 复制源代码
COPY mem0-core/src ./mem0-core/src
COPY mem0-server/src ./mem0-server/src
COPY mem0-sdk/src ./mem0-sdk/src
COPY mem0-example/src ./mem0-example/src

# 安装 Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# 编译项目
RUN mvn clean package -DskipTests

# 复制构建好的 jar 文件
COPY mem0-server/target/mem0-server-1.0.0.jar ./app.jar

# 创建日志目录
RUN mkdir -p /app/logs

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/test/health || exit 1

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"] 