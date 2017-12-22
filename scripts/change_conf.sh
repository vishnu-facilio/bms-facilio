#!/bin/bash

export APP_HOME="/home/ubuntu/tomcat"
export BMS_DIR="$APP_HOME/webapps/ROOT"
export CONF_DIR="$BMS_DIR/WEB-INF/classes/conf"
export META_INF_DIR="$BMS_DIR/META-INF"

echo "Deployment group is : $DEPLOYMENT_GROUP_NAME" >> /home/ubuntu/deployment.log
echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/ubuntu/deployment.log
echo "======" >> /home/ubuntu/deployment.log

unzip $APP_HOME/webapps/ROOT.war -d $APP_HOME/webapps/ROOT

if [ "$DEPLOYMENT_GROUP_NAME" = "pre_production" ]; then
    cp $META_INF_DIR/context-stage.xml $META_INF_DIR/context.xml
    sed -i -e 's/localhost:9090/app.facilio.in/g' $CONF_DIR/awsprops.properties
    sed -i'' "s%senvironment=.*%senvironment=stage%g" $CONF_DIR/awsprops.properties
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "production_deployment" ]; then
    cp $META_INF_DIR/context-production.xml $META_INF_DIR/context.xml
    sed -i'' "s%schedulerServer=.*%schedulerServer=false%g" $CONF_DIR/awsprops.properties
    sed -i -e 's/localhost:9090/app.fazilio.com/g' $CONF_DIR/awsprops.properties
    sed -i'' "s%senvironment=.*%senvironment=production%g" $CONF_DIR/awsprops.properties
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "production-scheduler" ]; then
    cp $META_INF_DIR/context-production.xml $META_INF_DIR/context.xml
    sed -i'' "s%senvironment=.*%senvironment=scheduler%g" $CONF_DIR/awsprops.properties
fi