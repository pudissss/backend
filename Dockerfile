FROM maven:3.9-eclipse-temurin-17

WORKDIR /app

# Copy the entire project
COPY . .

# Build the application
RUN mvn clean package -DskipTests

# Verify the JAR exists
RUN ls -la target/

# Set environment variables
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE="prod"

# Expose the port
EXPOSE 8081

# Run the application
CMD ["java", "-jar", "target/backend-0.0.1-SNAPSHOT.jar"] 