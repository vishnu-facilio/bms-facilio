#!/usr/bin/env bash
echo "starting the server..."
export FACILIO_HOME="/home/facilio"
export APP_HOME="$FACILIO_HOME/tomcat"
export CONF_DIR="$APP_HOME/webapps/ROOT/WEB-INF/classes/conf"
export JAVA_HOME=$FACILIO_HOME/jdk
export PATH=$JAVA_HOME:$PATH
export GOOGLE_APPLICATION_CREDENTIALS="/tmp/secrets/google_app_credentials.json"
cd $FACILIO_HOME

pid=`/home/facilio/jdk/bin/jps | grep Bootstrap| cut -d' ' -f1`
if [ -z "$pid" ]; then
    echo "Java process is not running"
else
    echo "Shutting down java process $pid"
    sudo kill -9 $pid
fi
chmod 644 $APP_HOME/logs/*
sh $APP_HOME/bin/startup.sh
rm -rf $FACILIO_HOME/deployment-files/*

echo "server started..."