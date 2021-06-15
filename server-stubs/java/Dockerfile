FROM openjdk:8-jdk-alpine

ARG JAR_FILE

COPY ${JAR_FILE} sample-app.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/sample-app.jar"]
