echo "stopping the server..."

export APP_HOME="/home/ubuntu/tomcat"
export CONF_DIR="$APP_HOME/webapps/ROOT/WEB-INF/classes/conf"

cd $APP_HOME

sh bin/shutdown.sh

rm -rf webapps/ROOT
rm -rf webapps/ROOT.war

echo "server stopped..."