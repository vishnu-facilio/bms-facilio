echo "starting the server..."

export APP_HOME="/home/ubuntu/apache-tomcat-9.0.0.M21"
export CONF_DIR="$APP_HOME/webapps/bms/WEB-INF/classes/conf"

cd /home/ubuntu

sudo killall -9 java
sudo sh replace_conf.sh
sudo sh $APP_HOME/bin/catalina.sh start

echo "server started..."