#!/usr/bin/env bash
echo "stopping the server..."

export APP_HOME="/home/ubuntu/tomcat"
export CONF_DIR="$APP_HOME/webapps/ROOT/WEB-INF/classes/conf"

cd $APP_HOME

sh bin/shutdown.sh
servername=`hostname`
servername=`grep "api.servername" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`
sudo aws s3 mv --recursive $APP_HOME/logs/ s3://facilio-server-logs/$servername/

rm -rf webapps/ROOT
rm -rf webapps/ROOT.war

echo "server stopped..."