#!/usr/bin/env bash
echo "stopping the server..."

export APP_HOME="/home/facilio/tomcat"
export CONF_DIR="$APP_HOME/webapps/ROOT/WEB-INF/classes/conf"

ipAddress=`hostname -I|awk '{$1=$1};1'`
servername=`grep "api.servername" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`
logsBucket=`grep "logs.bucket" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`
today=`date +%F-%H-%M-%S`

cd $APP_HOME
pid=`/home/facilio/jdk/bin/jps | grep Bootstrap| cut -d' ' -f1`
if [ -z "$pid" ]; then
    echo "Java process is not running"
    exit 0;
fi
sudo kill -9 $pid
# sh bin/shutdown.sh
sh /home/facilio/move_logs.sh
aws s3 mv $APP_HOME/logs/catalina.out s3://$logsBucket/$servername/$ipAddress/catalinaout.$today.log
sudo rm -rf $APP_HOME/logs/*
rm -rf webapps/ROOT
rm -rf webapps/ROOT.war

echo "server stopped..."