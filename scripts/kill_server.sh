#!/usr/bin/env bash
echo "stopping the server..."

export APP_HOME="/home/ubuntu/tomcat"
export CONF_DIR="$APP_HOME/webapps/ROOT/WEB-INF/classes/conf"

ipAddress=`hostname -I|awk '{$1=$1};1'`
servername=`grep "api.servername" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`
logsBucket=`grep "logs.bucket" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`
today=`date +%F-%H-%M-%S`

cd $APP_HOME

sh bin/shutdown.sh
sh /home/ubuntu/move_logs.sh
aws s3 mv $APP_HOME/logs/catalina.out s3://$logsBucket/$servername/$ipAddress/catalinaout.$today.log

rm -rf webapps/ROOT
rm -rf webapps/ROOT.war

echo "server stopped..."