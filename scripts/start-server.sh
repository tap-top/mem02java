#!/bin/bash

PORT=8080

cd "$(dirname "$0")/.."

echo "检查 $PORT 端口是否被占用..."
PID=$(lsof -ti tcp:$PORT)
if [ -n "$PID" ]; then
  echo "端口 $PORT 被进程 $PID 占用，正在杀掉..."
  kill -9 $PID
  echo "已杀掉进程 $PID"
else
  echo "端口 $PORT 未被占用"
fi

echo "启动 mem0-server..."
cd mem0-server
mvn spring-boot:run 