echo "starting the server..."
export UBUNTU_HOME="/home/ubuntu"
export APP_HOME="$UBUNTU_HOME/tomcat"
export CONF_DIR="$APP_HOME/webapps/ROOT/WEB-INF/classes/conf"

cd /home/ubuntu

sudo killall -9 java
cp $UBUNTU_HOME/*.jar $CONF_DIR/../
sudo sh $APP_HOME/bin/catalina.sh start
sudo chmod 644 $APP_HOME/logs/*

echo "server started..."