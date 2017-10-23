echo "stoping the server..."

export APP_HOME="/home/ubuntu/apache-tomcat-9.0.0.M21"
export CONF_DIR="$APP_HOME/webapps/bms/WEB-INF/classes/conf"

cd $APP_HOME

sudo sh bin/shutdown.sh

sudo rm -rf webapps/bms
sudo rm -rf webapps/bms.war

echo "server stopped..."