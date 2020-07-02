#!/usr/bin/env bash
echo "stopping the server..."

export APP_HOME="/home/facilio/tomcat"

cd $APP_HOME

pid=`/home/facilio/jdk/bin/jps | grep Bootstrap| cut -d' ' -f1`
if [ -z "$pid" ]; then
    echo "Java process is not running"
    exit 0;
fi
kill -9 $pid

echo "server stopped..."