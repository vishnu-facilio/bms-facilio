#!/bin/bash
echo "replacing conf files..."

export APP_HOME="/home/ubuntu/apache-tomcat-9.0.0.M21"
export CONF_DIR="$APP_HOME/webapps/bms/WEB-INF/classes/conf"

CURRENT_HOSTNAME=$(hostname)

API_SERVERS="ip-172-31-18-204"
SCHEDULE_SERVERS="ip-172-31-22-26"

for SCHEDULE_SERVER in $SCHEDULE_SERVERS
do
	if [ "$SCHEDULE_SERVER" = "$CURRENT_HOSTNAME" ]; then
		sed -i'' "s%schedulerServer=.*%schedulerServer=true%g" $CONF_DIR/awsprops.properties
	fi
done

for API_SERVER in $API_SERVERS
do
	if [ "$API_SERVER" = "$CURRENT_HOSTNAME" ]; then
		sed -i'' "s%schedulerServer=.*%schedulerServer=false%g" $CONF_DIR/awsprops.properties
	fi
done

echo "conf files replaced..."