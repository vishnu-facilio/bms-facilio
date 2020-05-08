#!/usr/bin/env bash

pid=`/home/ubuntu/jdk/bin/jps | grep Bootstrap| cut -d' ' -f1`
if [ -z "$pid" ]; then
    echo "Java process is not running"
    exit 0;
fi
export APP_HOME="/home/ubuntu/tomcat"
export CONF_DIR="$APP_HOME/webapps/ROOT/WEB-INF/classes/conf"
ipAddress=`hostname -I|awk '{$1=$1};1'`
logsBucket=facilio-server-logs
servername=`grep "app.domain=" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`
logsBucket=`grep "logs.bucket" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`

today=`date +%FT%H-%M`
logDir=threaddump_$today
mkdir $logDir
for i in 1 2 3 4 5;
do
/home/ubuntu/jdk/bin/jstack $pid > ${logDir}/td${i}.txt
sleep 5
done
zip -rq $logDir.zip $logDir/
aws s3 mv $logDir.zip s3://$logsBucket/$servername/$ipAddress/
rm -rf $logDir