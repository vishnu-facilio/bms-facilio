#!/usr/bin/env bash
echo "stopping the server..."

export FACILIO_HOME="/home/facilio"
export APP_HOME="$FACILIO_HOME/tomcat"
export CONF_DIR="$APP_HOME/webapps/ROOT/WEB-INF/classes/conf"


export deployment=`grep "deployment" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`

cd $APP_HOME

#kill server called from build upgrade. create a file so that app server local monitor won't do server start.
if [ "$LIFECYCLE_EVENT" = "ApplicationStop" ];
then
   touch $FACILIO_HOME/build_update
fi

pid=`/home/facilio/jdk/bin/jps | grep Bootstrap| cut -d' ' -f1`
if [ -z "$pid" ]; then
    echo "Java process is not running"
    exit 0;
fi
kill -9 $pid
echo "server stopped..."