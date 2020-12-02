#!/usr/bin/env bash

export APP_HOME="/home/facilio/tomcat"
export CONF_DIR="$APP_HOME/webapps/ROOT/WEB-INF/classes/conf"

ipAddress=`hostname -I|awk '{$1=$1};1'`
logsBucket=facilio-server-logs
servername=`grep "mainapp.domain=" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`
logsBucket=`grep "logs.bucket" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`

today=`date +%F`
logDir=`date +%Y/%m/%d`
for file in `ls $APP_HOME/logs`
do
        extension=`echo $file | rev | cut -d"." -f1 | rev`
        if [ "$extension" = "hprof" ]; then
                gzip $APP_HOME/logs/$file
                if [ -f $APP_HOME/logs/$file ]; then
                        aws s3 mv $APP_HOME/logs/$file "s3://$logsBucket/$logDir/$servername/$ipAddress/"
                fi
                if [ -f $APP_HOME/logs/$file".gz" ]; then
                        aws s3 mv $APP_HOME/logs/$file".gz" "s3://$logsBucket/$logDir/$servername/$ipAddress/"
                fi
        fi
done