#!/usr/bin/env bash

export APP_HOME="/home/ubuntu/tomcat"
export CONF_DIR="$APP_HOME/webapps/ROOT/WEB-INF/classes/conf"

ipAddress=`hostname -I|awk '{$1=$1};1'`
logsBucket=facilio-server-logs
servername=`grep "api.servername" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`
logsBucket=`grep "logs.bucket" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`

today=`date +%Y/%m/%d`
for file in `ls $APP_HOME/logs`
do
    if [ "$file" != "catalina.out" -a "$file" != "serverlog" -a "$file" != "resultset" -a "$file" != "l4jaccesslog" ]; then
        dateString=`echo $file | cut -d '.' -f 2 | cut -d '_' -f 1`
        if [ $dateString != $today ]; then
            sudo aws s3 mv $APP_HOME/logs/$file s3://$logsBucket/$servername/$dateString/$ipAddress/
        fi
    fi
done

