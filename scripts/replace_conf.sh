#!/bin/bash
echo "replacing conf files..."

export APP_HOME="/home/ubuntu/apache-tomcat-9.0.0.M21"
export BMS_DIR="$APP_HOME/webapps/bms"
export CONF_DIR="$BMS_DIR/WEB-INF/classes/conf"
export META_INF_DIR="$BMS_DIR/META-INF"

echo "copying $DEPLOYMENT_GROUP_NAME context file"

if [ "$DEPLOYMENT_GROUP_NAME" == "pre_production" ]
then
    cp $META_INF_DIR/context-stage.xml $META_INF_DIR/context.xml
fi

if [ "$DEPLOYMENT_GROUP_NAME" == "production_deployment" ]
then
    cp $META_INF_DIR/context-production.xml $META_INF_DIR/context.xml
fi

echo "copied $DEPLOYMENT_GROUP_NAME context file"

CURRENT_HOSTNAME=$(hostname)

API_SERVERS="ip-172-31-21-167"
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
