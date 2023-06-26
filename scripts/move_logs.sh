#!/usr/bin/env bash

export APP_HOME="/home/facilio/tomcat"
export CONF_DIR="$APP_HOME/webapps/ROOT/WEB-INF/classes/conf"

ipAddress=`hostname -I|awk '{$1=$1};1'`
logsBucket=facilio-server-logs
servername=`grep "mainapp.domain=" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`
logsBucket=`grep "logs.bucket" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`
identityBucket=`grep "identityBucket" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`

deployment=`grep "deployment" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`

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

curSerLog=`ls -t  tomcat/logs/serverlog* | head -n 1 | cut -f3 -d "/"`
isCleanLog=0
totalArg=$#
if test $totalArg -ge 1; then
        isCleanLog=$1
fi

today=`date +%F`
logDir=`date +%Y/%m/%d`

if [ -f "$APP_HOME/logs/identity_auditlog" ]; then
aws s3 cp $APP_HOME/logs/identity_auditlog "s3://$identityBucket/$logDir/$servername/$ipAddress/"
fi

for file in `ls $APP_HOME/logs`
do
    if [ "$file" != "catalina.out" -a "$file" != "$curSerLog"  -a "$file" != "accesslog.$today" -a "$file" != "serverlog" -a "$file" != "resultset" -a "$file" != "l4jaccesslog"  -a "$file" != "useraccesslog"  -a "$file" != "clientlog"  -a "$file" != "userlogin"  -a "$file" != "sqlitelog" ]; then
        dateString=`echo $file | cut -d '.' -f 2 | cut -d '_' -f 1`
        if test $isCleanLog -eq 1 ||  test $dateString != $today; then
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
find $APP_HOME/temp/* -mmin +1440 -delete
find . -name $APP_HOME/temp/* -print0 | xargs -0 rm
