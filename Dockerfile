# Bước 1: Dùng Maven để build file jar
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Bước 2: Dùng OpenJDK siêu nhẹ để chạy file jar nhằm tiết kiệm bộ nhớ trên Render
FROM openjdk:17-jdk-slim
COPY --from=build /target/*.jar xanhdi-app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "xanhdi-app.jar"]