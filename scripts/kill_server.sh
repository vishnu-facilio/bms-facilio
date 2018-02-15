#!/usr/bin/env bash
echo "stopping the server..."

export APP_HOME="/home/ubuntu/tomcat"
export CONF_DIR="$APP_HOME/webapps/ROOT/WEB-INF/classes/conf"

servername=`hostname`
servername=`grep "api.servername" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`

today=`date +%F-%H-%M-%S`

cd $APP_HOME

sh bin/shutdown.sh
sh /home/ubuntu/move_logs.sh
sudo aws s3 mv $APP_HOME/logs/catalina.out s3://facilio-server-logs/$servername/catalinaout.$today.log

rm -rf webapps/ROOT
rm -rf webapps/ROOT.war

echo "server stopped..."