#!/usr/bin/env bash
echo "starting the server..."
export UBUNTU_HOME="/home/ubuntu"
export APP_HOME="$UBUNTU_HOME/tomcat"
export CONF_DIR="$APP_HOME/webapps/ROOT/WEB-INF/classes/conf"
export JAVA_HOME=$UBUNTU_HOME/jdk
export PATH=$JAVA_HOME:$PATH
export GOOGLE_APPLICATION_CREDENTIALS="/tmp/secrets/google_app_credentials.json"
cd $UBUNTU_HOME

pid=`/home/ubuntu/jdk/bin/jps | grep Bootstrap| cut -d' ' -f1`
if [ -z "$pid" ]; then
    echo "Java process is not running"
else
    echo "Shutting down java process $pid"
    sudo kill -9 $pid
fi
chmod 644 $APP_HOME/logs/*
sh $APP_HOME/bin/startup.sh
rm -rf $UBUNTU_HOME/deployment-files/*

echo "server started..."