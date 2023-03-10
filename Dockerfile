FROM maven:3.8.7-eclipse-temurin-19-alpine AS MAVEN_BUILD
WORKDIR /app
COPY . .
RUN mvn clean package -Dmaven.test.skip=true

FROM openjdk:19-jdk-alpine3.16
COPY --from=MAVEN_BUILD /app/target/*.jar /app/application.jar
EXPOSE 8080


ENV env=dev
ENV storage=qiniu

ENV ak=ak
ENV sk=sk

ENV bucket=bucket
ENV mongoUri=mongo
ENV urlPrefix=http://localhost:8080
ENV token=hello

ENTRYPOINT ["java", "-jar", "/app/application.jar"]