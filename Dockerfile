# 기본 이미지를 설정합니다
FROM openjdk:17-jdk-slim

# 애플리케이션이 실행될 포트
EXPOSE 8080

# 애플리케이션 실행 명령어
CMD ["java", "-jar", "/*.jar"]
