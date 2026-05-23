# ---- Steg 1: Bygg appen ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Kopier Gradle-wrapper og byggefiler først (utnytter Docker-caching)
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon || true

# Kopier kildekoden og bygg en kjørbar jar (hopper over tester for raskere bygg)
COPY src src
RUN ./gradlew bootJar --no-daemon -x test

# ---- Steg 2: Kjør appen ----
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
