#!/bin/bash

export UBUNTU_HOME="/home/ubuntu"
export APP_HOME="$UBUNTU_HOME/tomcat"
export BMS_DIR="$APP_HOME/webapps/ROOT"
export CLASSES_DIR="$BMS_DIR/WEB-INF/classes"
export CONF_DIR="$CLASSES_DIR/conf"
export META_INF_DIR="$BMS_DIR/META-INF"

echo `date` " : Deployment group is : $DEPLOYMENT_GROUP_NAME" >> /home/ubuntu/deployment.log
echo "======" >> /home/ubuntu/deployment.log

unzip $APP_HOME/webapps/ROOT.war -d $APP_HOME/webapps/ROOT

if [ "$DEPLOYMENT_GROUP_NAME" = "pre_production" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/ubuntu/deployment.log
    cp $UBUNTU_HOME/deployment-files/context-stage.xml $META_INF_DIR/context.xml
    cp $UBUNTU_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    cp $UBUNTU_HOME/deployment-files/awsprops-stage.properties $CONF_DIR/awsprops.properties
    cp $UBUNTU_HOME/deployment-files/log4j-stage.properties $CLASSES_DIR/log4j.properties
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/ubuntu/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "production_deployment" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/ubuntu/deployment.log
    cp $UBUNTU_HOME/deployment-files/context-production.xml $META_INF_DIR/context.xml
    cp $UBUNTU_HOME/deployment-files/log4j-user.properties $CLASSES_DIR/log4j.properties
    cp $UBUNTU_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    cp $UBUNTU_HOME/deployment-files/awsprops-user.properties $CONF_DIR/awsprops.properties
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/ubuntu/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "production-scheduler" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/ubuntu/deployment.log
    cp $UBUNTU_HOME/deployment-files/context-production.xml $META_INF_DIR/context.xml
    cp $UBUNTU_HOME/deployment-files/log4j-scheduler.properties $CLASSES_DIR/log4j.properties
    cp $UBUNTU_HOME/deployment-files/awsprops-scheduler.properties $CONF_DIR/awsprops.properties
    cp $UBUNTU_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/ubuntu/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "production-kinesis" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/ubuntu/deployment.log
    cp $UBUNTU_HOME/deployment-files/context-production.xml $META_INF_DIR/context.xml
    cp $UBUNTU_HOME/deployment-files/log4j-kinesis.properties $CLASSES_DIR/log4j.properties
    cp $UBUNTU_HOME/deployment-files/awsprops-kinesis.properties $CONF_DIR/awsprops.properties
    cp $UBUNTU_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/ubuntu/deployment.log
fi