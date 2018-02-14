#!/usr/bin/env bash

export APP_HOME="/home/ubuntu/tomcat"
export CONF_DIR="$APP_HOME/webapps/ROOT/WEB-INF/classes/conf"

servername=`hostname`
servername=`grep "api.servername" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`

today=`date +%F`
for file in `ls $APP_HOME/logs`
do
    if [ "$file" != "catalina.out" -a "$file" != "serverlog" ]; then
        dateString=`echo $file | cut -d '.' -f 2`
        if [ $dateString != $today ]; then
            sudo aws s3 mv $APP_HOME/logs/$file s3://facilio-server-logs/$servername/
        fi
    fi
done

