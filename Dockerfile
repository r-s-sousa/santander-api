FROM gradle:8-jdk21-alpine AS build

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle ./gradle
COPY src ./src

RUN chmod +x gradlew

RUN ./gradlew build

FROM amazoncorretto:21-alpine-jdk

COPY --from=build /app/build/libs/address.api-1.0.0.jar /application.jar

COPY --from=build /app/src/main/resources /src/main/resources

ENTRYPOINT ["java", "-jar", "application.jar"]