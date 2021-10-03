FROM maven:3.8.2-adoptopenjdk-16 as build
WORKDIR /app
COPY pom.xml ./
COPY src/ ./src
COPY ci/maven-settings.xml ./settings.xml
RUN mvn -s ./settings.xml package -DskipTests

FROM adoptopenjdk:16-jre
WORKDIR /app
COPY --from=build /app/target/*.jar ./app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
