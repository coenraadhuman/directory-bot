FROM eclipse-temurin:21-jdk-jammy AS builder

ARG APP_UID=1000
ARG APP_GID=1000

WORKDIR /build
COPY . .
RUN ./gradlew clean build

FROM gcr.io/distroless/java21-debian12

ENV APP_UID=1000
ENV APP_GID=1000

WORKDIR /config
COPY --from=builder --chown=${APP_UID}:${APP_GID} /build/build/resources/main/directory-bot.properties directory-bot.properties

WORKDIR /app
COPY --from=builder --chown=${APP_UID}:${APP_GID} /build/build/libs/directory-bot-*.jar directory-bot.jar

ENTRYPOINT ["java", "-jar", "directory-bot.jar"]