#!/bin/bash

export FACILIO_HOME="/home/facilio"
export APP_HOME="$FACILIO_HOME/tomcat"
export BMS_DIR="$APP_HOME/webapps/ROOT"
export CLASSES_DIR="$BMS_DIR/WEB-INF/classes"
export CONF_DIR="$CLASSES_DIR/conf"
export META_INF_DIR="$BMS_DIR/META-INF"

echo `date` " : Deployment group is : $DEPLOYMENT_GROUP_NAME" >> /home/facilio/deployment.log
echo "======" >> /home/facilio/deployment.log

unzip -o $APP_HOME/webapps/ROOT.war -d $APP_HOME/webapps/ROOT

if [ "$DEPLOYMENT_GROUP_NAME" = "pre_production" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    cp $FACILIO_HOME/deployment-files/awsprops-stage.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-files/log4j-stage.properties $CLASSES_DIR/log4j.properties
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "production_deployment" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-files/log4j-user.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    cp $FACILIO_HOME/deployment-files/awsprops-user.properties $CONF_DIR/awsprops.properties
    cp $FACILIO-HOME/deployment-files/agentIntegration-production.properties $CONF_DIR/agentIntegration.properties
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "production-scheduler" ] || [ "$DEPLOYMENT_GROUP_NAME" = "production-scheduler-app" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-files/log4j-scheduler.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/deployment-files/awsprops-scheduler.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "sp-production-scheduler" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-files/log4j-sp-scheduler.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/deployment-files/awsprops-sp-scheduler.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/setenv-sp.sh $APP_HOME/bin/setenv.sh
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "sp-production-user" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-files/log4j-sp-user.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/deployment-files/awsprops-sp-user.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/setenv-sp.sh $APP_HOME/bin/setenv.sh
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi


if [ "$DEPLOYMENT_GROUP_NAME" = "production-kinesis" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-files/log4j-kinesis.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/deployment-files/awsprops-kinesis.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "demo-user" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-files/log4j-user.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    cp $FACILIO_HOME/deployment-files/awsprops-demo.properties $CONF_DIR/awsprops.properties
    cp $FACILIO-HOME/deployment-files/agentIntegration-production.properties $CONF_DIR/agentIntegration.properties
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi
