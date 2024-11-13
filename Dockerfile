FROM openjdk:latest
COPY ./target/devops.jar /tmp
WORKDIR /tmp
RUN mkdir -p /tmp/output
ENTRYPOINT ["java", "-jar", "devops.jar", "world:3306", "10000"]