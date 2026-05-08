FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN groupadd -r cryptonest && useradd -r -g cryptonest cryptonest
USER cryptonest
COPY portfolio-service/target/*.jar app.jar
EXPOSE 8082
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s \
  CMD wget -qO- http://localhost:8082/actuator/health || exit 1
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
