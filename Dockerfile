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

ARG AWS_ELASTICACHE_REDIS_ENDPOINT
ARG AWS_ELASTICACHE_REDIS_WITH_SSL
ARG ENVIRONMENT
ARG POSTGRESQL_PASSWORD
ARG POSTGRESQL_URL
ARG POSTGRESQL_USERNAME

ENV AWS_ELASTICACHE_REDIS_ENDPOINT=$AWS_ELASTICACHE_REDIS_ENDPOINT
ENV AWS_ELASTICACHE_REDIS_WITH_SSL=$AWS_ELASTICACHE_REDIS_WITH_SSL
ENV ENVIRONMENT=$ENVIRONMENT
ENV POSTGRESQL_PASSWORD=$POSTGRESQL_PASSWORD
ENV POSTGRESQL_URL=$POSTGRESQL_URL
ENV POSTGRESQL_USERNAME=$POSTGRESQL_USERNAME

ENTRYPOINT ["java", "-jar", "application.jar"]