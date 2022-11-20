FROM amazoncorretto:11-alpine-jdk
MAINTAINER example.com
COPY target/accounting.jar accounting.jar
ENTRYPOINT ["java","-jar","/accounting.jar"]