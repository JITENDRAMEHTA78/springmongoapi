#!/usr/bin/env sh

./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=dev -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8081"

