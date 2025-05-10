# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml first to cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# List contents of target directory to verify build
RUN ls -la target/

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the JAR file from build stage
COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar ./app.jar

# List contents to verify JAR exists
RUN ls -la

# Set environment variables
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE="prod"

# Expose the port
EXPOSE 8081

# Run the application
CMD ["java", "-jar", "app.jar"] 