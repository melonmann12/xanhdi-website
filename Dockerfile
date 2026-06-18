# Bước 1: Dùng Maven để build file jar
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Bước 2: Dùng Eclipse Temurin Java 17 (Cực nhẹ và cực kỳ ổn định trên các nền tảng Cloud)
FROM eclipse-temurin:17-jre-jammy
COPY --from=build /target/*.jar xanhdi-app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "xanhdi-app.jar"]