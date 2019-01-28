FROM openjdk:10-jdk-alpine
ARG BUILD_VERSION

ADD m-scheduler-${BUILD_VERSION}.jar m-scheduler.jar

EXPOSE 8888

ENTRYPOINT exec java -jar /m-scheduler.jar
