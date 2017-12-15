#!/bin/bash

export APP_HOME="/home/ubuntu/tomcat"
export BMS_DIR="$APP_HOME/webapps/ROOT"
export META_INF_DIR="$BMS_DIR/META-INF"

echo "Deployment group is : $DEPLOYMENT_GROUP_NAME" >> /home/ubuntu/deployment.log
echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/ubuntu/deployment.log
echo "======" >> /home/ubuntu/deployment.log

unzip $APP_HOME/webapps/ROOT.war -d $APP_HOME/webapps/ROOT

if [ "$DEPLOYMENT_GROUP_NAME" = "pre_production" ]; then
    cp $META_INF_DIR/context-stage.xml $META_INF_DIR/context.xml
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "production_deployment" ]; then
    cp $META_INF_DIR/context-production.xml $META_INF_DIR/context.xml
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "production-scheduler" ]; then
	sed -i'' "s%schedulerServer=.*%schedulerServer=true%g" $CONF_DIR/awsprops.properties
fi