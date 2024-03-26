FROM maven:3.9.4-amazoncorretto-21 AS MAVEN_BUILD
WORKDIR /app
COPY . .
RUN mvn clean package -Dmaven.test.skip=true

FROM openjdk:21-rc-oraclelinux8
COPY --from=MAVEN_BUILD /app/target/*.jar /app/application.jar
EXPOSE 8080


ENTRYPOINT ["java","--enable-preview", "-jar", "/app/application.jar"]