FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /build
COPY ../.. .
RUN ./gradlew clean build

FROM eclipse-temurin:21-jre-jammy

RUN apt-get update && apt-get install -y gosu
RUN curl -fsSL "https://raw.githubusercontent.com/filebot/plugins/master/gpg/maintainer.pub" | gpg --dearmor --output "/usr/share/keyrings/filebot.gpg"  \
     && echo "deb [arch=all signed-by=/usr/share/keyrings/filebot.gpg] https://get.filebot.net/deb/ universal main" > /etc/apt/sources.list.d/filebot.list \
     && apt-get update \
     && apt-get install -y --install-recommends filebot \
     && which filebot

WORKDIR /config
COPY --from=builder /build/build/resources/main/directory-bot.properties directory-bot.properties

WORKDIR /app
COPY --from=builder /build/build/libs/directory-bot-*.jar directory-bot.jar

WORKDIR /
COPY --from=builder /build/src/docker/filebot-entrypoint.sh entrypoint.sh
RUN chmod +x entrypoint.sh

ENTRYPOINT ["./entrypoint.sh", "java", "-jar", "/app/directory-bot.jar"]