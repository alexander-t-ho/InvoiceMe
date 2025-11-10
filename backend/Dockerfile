FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle
RUN gradle build --no-daemon -x test || return 0
COPY . .
RUN gradle build --no-daemon -x test

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]


