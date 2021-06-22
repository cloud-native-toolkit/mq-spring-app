# multistage Dockerfile
# first stage does the maven build
# second stage creates the runtime image
###  stage 1 ###
FROM adoptopenjdk/maven-openjdk11 as builder

WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN mvn package -DskipTests

### stage 2  ###


FROM registry.access.redhat.com/ubi8/ubi:8.4

RUN dnf install -y java-11-openjdk.x86_64

COPY --from=builder /workspace/app/target/*.jar ./app.jar

EXPOSE 8080/tcp
USER 1001

CMD ["java", "-jar", "./app.jar"]
