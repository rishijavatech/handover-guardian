# ============================
# Build stage
# ============================
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# ============================
# Runtime stage
# ============================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# default to dev, can be overridden
ENV SPRING_PROFILES_ACTIVE=dev

# Install Postgres client (pg_isready) + Bash + Netcat (for Redis check)
RUN apk add --no-cache postgresql-client bash netcat-openbsd

# Copy JAR
COPY --from=build /app/target/guardian-service.jar app.jar

# Copy wait-for script
COPY wait-for.sh .
RUN chmod +x wait-for.sh

# Create logs dir (optional)
RUN mkdir -p /app/logs

# Metadata
EXPOSE 8082

# Wait for guardian-db and redis, then start
ENTRYPOINT ["./wait-for.sh", "guardian-db", "redis", "java", "-jar", "app.jar"]