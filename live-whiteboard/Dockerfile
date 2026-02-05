# ======================
# Build stage
# ======================
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw

# ⛔ REMOVED dependency:go-offline

COPY src src
RUN ./mvnw clean package -DskipTests


# ======================
# Run stage
# ======================
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY --from=build /app/target/*SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java","-jar","app.jar"]
