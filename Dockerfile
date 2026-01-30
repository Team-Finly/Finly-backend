# =========================
# 1. Build stage
# =========================
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# 전체 소스 복사
COPY . .

# CI에서 이미 test를 돌리므로 Docker build 단계에서는 테스트 스킵
RUN chmod +x ./gradlew && ./gradlew clean bootJar -x test --no-daemon


# =========================
# 2. Runtime stage
# =========================
FROM eclipse-temurin:21-jre
WORKDIR /app

# 타임존 설정 (OS + JVM)
ENV TZ=Asia/Seoul

# 빌드 결과물 복사 (jar 이름 고정 ❌, 버전 바뀌어도 OK)
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Duser.timezone=Asia/Seou]()
