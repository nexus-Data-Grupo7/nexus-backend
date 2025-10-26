FROM openjdk:21-jdk

RUN microdnf install -y curl

WORKDIR /app

ARG JAR_URL="https://github.com/nexus-Data-Grupo7/nexus-backend/releases/download/v1.0.9/leitor-base-de-dados-nexus-1.0-SNAPSHOT.jar"

RUN curl -L -o app.jar ${JAR_URL}

EXPOSE 8080

CMD [ "java", "-jar", "app.jar" ]