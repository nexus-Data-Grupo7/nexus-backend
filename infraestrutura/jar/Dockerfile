# 1. Mude a tag para '21-jdk-slim'
FROM eclipse-temurin:21-jdk

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

ARG JAR_URL="https://github.com/nexus-Data-Grupo7/nexus-backend/releases/download/v1.1.6/leitor-base-de-dados-nexus-1.0-SNAPSHOT.jar"

RUN curl -L -o app.jar ${JAR_URL}

EXPOSE 8080

CMD [ "java", "-jar", "app.jar" ]