FROM maven:3.8.7-openjdk-18 AS MAVEN_BUILD

COPY pom.xml /build/
COPY src /build/src/

WORKDIR /build/
RUN mvn package

# FROM openjdk:8-jre-alpine
FROM openjdk:18.0.2-jdk

WORKDIR /app

COPY --from=MAVEN_BUILD /build/target/demo-0.0.1-SNAPSHOT.jar /app/

ENTRYPOINT ["java", "-jar", "demo-0.0.1-SNAPSHOT.jar"]