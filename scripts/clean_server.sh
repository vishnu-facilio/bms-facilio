#!/usr/bin/env bash
echo "stopping the server..."

export APP_HOME="/home/facilio/tomcat"
export CONF_DIR="$APP_HOME/webapps/ROOT/WEB-INF/classes/conf"

ipAddress=`hostname -I|awk '{$1=$1};1'`
servername=`grep "mainapp.domain=" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`
logsBucket=`grep "logs.bucket" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`
today=`date +%F-%H-%M-%S`

todayDateOnly=`date +%F`
logDir=`date +%Y/%m/%d`

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


cd $APP_HOME
pid=`/home/facilio/jdk/bin/jps | grep Bootstrap| cut -d' ' -f1`
if [ -z "$pid" ]; then
    echo "Java process is not running"
else
    echo "Shutting down java process $pid"
    kill -9 $pid
fi
# sh bin/shutdown.sh
sh /home/facilio/move_logs.sh
aws s3 mv $APP_HOME/logs/catalina.out s3://$logsBucket/$logDir/$servername/$server_type/$ipAddress/catalinaout.$today.log

if [ -f $APP_HOME/logs/l4jaccesslog ]; then
	mv $APP_HOME/logs/l4jaccesslog $APP_HOME/logs/l4jaccesslog.$today 
	gzip $APP_HOME/logs/l4jaccesslog.$today
	aws s3 mv $APP_HOME/logs/l4jaccesslog.$today.gz  "s3://$logsBucket/$logDir/$servername/$server_type/$ipAddress/"
fi

if [ -f $APP_HOME/logs/accesslog.$todayDateOnly ]; then
	mv $APP_HOME/logs/accesslog.$todayDateOnly $APP_HOME/logs/accesslog.$today 
	gzip $APP_HOME/logs/accesslog.$today
	aws s3 mv $APP_HOME/logs/accesslog.$today.gz  "s3://$logsBucket/$logDir/$servername/$server_type/$ipAddress/"
fi


if [ -f $APP_HOME/logs/serverlog.$todayDateOnly ]; then
	mv $APP_HOME/logs/serverlog.$todayDateOnly $APP_HOME/logs/serverlog.$today 
	gzip $APP_HOME/logs/serverlog.$today
	aws s3 mv $APP_HOME/logs/serverlog.$today.gz  "s3://$logsBucket/$logDir/$servername/$server_type/$ipAddress/"
fi

if [ -f $APP_HOME/logs/serverlog ]; then
	mv $APP_HOME/logs/serverlog $APP_HOME/logs/serverlog.$today 
	gzip $APP_HOME/logs/serverlog.$today
	aws s3 mv $APP_HOME/logs/serverlog.$today.gz  "s3://$logsBucket/$logDir/$servername/$server_type/$ipAddress/"
fi


rm -rf $APP_HOME/logs/*
rm -rf webapps/ROOT
rm -rf webapps/ROOT.war

echo "server stopped..."