FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S cryptonest && adduser -S cryptonest -G cryptonest
USER cryptonest
COPY notification-service/target/*.jar app.jar
EXPOSE 8084
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s \
  CMD wget -qO- http://localhost:8084/actuator/health || exit 1
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
