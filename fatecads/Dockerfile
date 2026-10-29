# syntax=docker/dockerfile:1
FROM maven:3.9.12-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml ./
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B -ntp -DskipTests package

FROM eclipse-temurin:17-jre-jammy
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --gid 10001 fatecads \
    && useradd --uid 10001 --gid fatecads --no-create-home fatecads
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar
USER 10001:10001
ENV SERVER_PORT=8081
EXPOSE 8081
HEALTHCHECK --interval=10s --timeout=5s --start-period=40s --retries=12 \
    CMD curl --fail --silent --output /dev/null "http://127.0.0.1:${SERVER_PORT}/login" || exit 1
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
