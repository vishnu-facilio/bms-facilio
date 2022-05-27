#!/usr/bin/env bash

export APP_HOME="/home/facilio/tomcat"
export CONF_DIR="$APP_HOME/webapps/ROOT/WEB-INF/classes/conf"

ipAddress=`hostname -I|awk '{$1=$1};1'`
logsBucket=facilio-server-logs
servername=`grep "mainapp.domain=" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`
logsBucket=`grep "logs.bucket" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`

isScheduler=`grep "schedulerServer=" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`
isMessageProcessor=`grep "messageProcessor=" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`
server_type=""
if [ "$isMessageProcessor" = "true" ] && [ "$isScheduler" = "true" ]; then
	server_type="message-and-scheduler"
elif [ "$isMessageProcessor" = "true" ]; then
	server_type="message-processor"
elif [ "$isScheduler" = "true" ]; then
	server_type="scheduler"
else 
	server_type="user"
fi


today=`date +%F`
logDir=`date +%Y/%m/%d`
for file in `ls $APP_HOME/logs`
do
    if [ "$file" != "catalina.out" -a "$file" != "serverlog.$today" -a "$file" != "serverlog" -a "$file" != "resultset" -a "$file" != "l4jaccesslog" ]; then
        dateString=`echo $file | cut -d '.' -f 2 | cut -d '_' -f 1`
        if [ $dateString != $today ]; then
            gzip $APP_HOME/logs/$file
            if [ -f $APP_HOME/logs/$file ]; then
                aws s3 mv $APP_HOME/logs/$file "s3://$logsBucket/$logDir/$servername/$server_type/$ipAddress/"
            fi
            if [ -f $APP_HOME/logs/$file".gz" ]; then
                aws s3 mv $APP_HOME/logs/$file".gz" "s3://$logsBucket/$logDir/$servername/$server_type/$ipAddress/"
            fi
        fi
    fi
done