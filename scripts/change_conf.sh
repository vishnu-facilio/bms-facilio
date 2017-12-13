#!/bin/bash

export APP_HOME="/home/ubuntu/apache-tomcat-9.0.0.M21"
export BMS_DIR="$APP_HOME/webapps/bms"
export META_INF_DIR="$BMS_DIR/META-INF"

echo "Deployment group is : $DEPLOYMENT_GROUP_NAME" >> /home/ubuntu/deployment.log
echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/ubuntu/deployment.log
echo "======" >> /home/ubuntu/deployment.log
printenv >> /home/ubuntu/deployment.log

unzip $APP_HOME/webapps/bms.war -d $APP_HOME/webapps/bms

if [ "$DEPLOYMENT_GROUP_NAME" = "pre_production" ]; then
    cp $META_INF_DIR/context-stage.xml $META_INF_DIR/context.xml
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "production_deployment" ]; then
    cp $META_INF_DIR/context-production.xml $META_INF_DIR/context.xml
fi