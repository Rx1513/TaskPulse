# syntax=docker/dockerfile:1
ARG BASE_IMAGE=registry.altlinux.org/altlinux/base:latest
ARG JAVA_PKG=java-21-openjdk
ARG MAVEN_PKG=maven
ARG APP_JAR=TaskPulse-1.0-SNAPSHOT.jar

FROM ${BASE_IMAGE} AS build
ARG JAVA_PKG
ARG MAVEN_PKG
ARG APP_JAR

RUN apt-get update \
    && apt-get install -y ${JAVA_PKG} ${MAVEN_PKG} \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY pom.xml ./
COPY src ./src
RUN mvn -DskipTests package

FROM ${BASE_IMAGE}
ARG JAVA_PKG
ARG APP_JAR

RUN apt-get update \
    && apt-get install -y ${JAVA_PKG} \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /opt/app
COPY --from=build /app/target/${APP_JAR} /opt/app/app.jar

EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /opt/app/app.jar"]
