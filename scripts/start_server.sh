echo "starting the server..."

export APP_HOME="/home/ubuntu/tomcat"
export CONF_DIR="$APP_HOME/webapps/ROOT/WEB-INF/classes/conf"

cd /home/ubuntu

sudo killall -9 java
sudo sh $APP_HOME/bin/catalina.sh start
sudo chmod 644 $APP_HOME/logs/*

echo "server started..."