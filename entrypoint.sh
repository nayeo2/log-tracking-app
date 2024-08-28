#!/bin/sh

# 가장 최근의 JAR 파일을 찾습니다
JAR_FILE=$(ls -t /app/logging-sample-prj-*-no_db.jar | head -n 1)

# JAR 파일이 존재하는지 확인합니다
if [ -z "$JAR_FILE" ]; then
  echo "No JAR file found"
  exit 1
fi

# JAR 파일을 실행합니다
exec java -jar "$JAR_FILE"
