# Build stage
FROM maven:3.9-eclipse-temurin-22 AS build
WORKDIR /app
COPY pom.xml .
# Download dependencies first to leverage Docker cache
RUN mvn dependency:go-offline -B
# Copy the rest of the project files (ignores files listed in .dockerignore)
COPY . .
# Build the application
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:22-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/food-delivery-system.war app.war
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.war"]
