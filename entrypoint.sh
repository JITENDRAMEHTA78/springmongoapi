#!/usr/bin/env sh

exec java -Dspring.profiles.active=$PROFILE -Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -jar app.jar "$@"
