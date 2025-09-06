# --- build stage ---
FROM gradle:8.7-jdk17 AS build
WORKDIR /app
COPY . .
# 캐시를 살리려면 필요 파일만 먼저 복사 → 이후 전체 복사 전략도 가능
RUN gradle clean bootJar --no-daemon -x test

# --- run stage ---
FROM eclipse-temurin:17-jre
WORKDIR /app
# 빌드 산출물 경로는 보통 build/libs/*.jar (스프링부트 플러그인)
COPY --from=build /app/build/libs/*.jar app.jar

# 운영 시 JVM 메모리/파일 인코딩 옵션 등 필요 시 추가
ENV JAVA_OPTS=""

EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
