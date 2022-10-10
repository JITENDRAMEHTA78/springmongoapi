FROM maven:3-jdk-8 AS build
WORKDIR /app
  
COPY pom.xml .
COPY mvnw .
COPY mvnw.cmd .

RUN mvn -B -DskipTests --fail-never --show-version -DinstallAtEnd=true -DdeployAtEnd=true dependency:go-offline

COPY src /app/src

RUN mvn clean -B -DskipTests --show-version -DinstallAtEnd=true -DdeployAtEnd=true package

FROM openjdk:8-jdk-alpine AS runtime

ENV PROFILE=dev

WORKDIR /app

COPY entrypoint.sh .
RUN chmod +x entrypoint.sh

COPY --from=build /app/target/final.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["/app/entrypoint.sh"]
