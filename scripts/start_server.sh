echo "starting the server..."

export APP_HOME="/home/ubuntu/tomcat"
export CONF_DIR="$APP_HOME/webapps/ROOT/WEB-INF/classes/conf"

cd /home/ubuntu

killall -9 java
sh replace_conf.sh
sh $APP_HOME/bin/catalina.sh start

echo "server started..."