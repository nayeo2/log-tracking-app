# 기본 이미지를 설정합니다
FROM openjdk:17-jdk-slim

# 작업 디렉토리 생성
WORKDIR /app

# 빌드된 JAR 파일을 복사합니다
# (가장 최근의 JAR 파일을 선택하기 위해 스크립트를 사용할 예정입니다)
COPY build/custom-libs /app/

# JAR 파일을 자동으로 찾고 실행하는 스크립트를 추가합니다
COPY entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh

# 엔트리포인트 스크립트를 실행하여 JAR 파일을 실행합니다
ENTRYPOINT ["/app/entrypoint.sh"]
