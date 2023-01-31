#!/usr/bin/env bash

export FACILIO_HOME="/home/facilio"
if [ "$LIFECYCLE_EVENT" != "ApplicationStart" ];
then
if test -f "$FACILIO_HOME/build_update"; then
    echo "Build upgrade is in progress. No restart required"
    exit 0;
 fi
 fi
 
 echo "starting the server..."
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
    kill -9 $pid
    echo "Sleeping 15 seconds to release the occupied ports"
    sleep 15
fi
chmod 644 $APP_HOME/logs/*
sh $APP_HOME/bin/startup.sh
rm -rf $FACILIO_HOME/deployment-files/*

#remove build_update file so that monit will monitor and start the server if it quits
if [ "$LIFECYCLE_EVENT" = "ApplicationStart" ];
then
        rm -rf $APP_HOME/temp/*
        rm $FACILIO_HOME/build_update
fi
sh start_hydra.sh
echo "server started..."