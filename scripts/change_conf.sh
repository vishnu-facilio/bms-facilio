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

if [ "$DEPLOYMENT_GROUP_NAME" = "pre_production" ] || [ "$DEPLOYMENT_GROUP_NAME" = "pre_production_new" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    cp $FACILIO_HOME/deployment-props/awsprops-stage.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-props/log4j-stage.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/deployment-props/service-stage.yml $CONF_DIR/service.yml
    cp $FACILIO_HOME/deployment-props/clientAppConfig-stage.yml $CONF_DIR/clientAppConfig.yml
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    echo "copied service file is =======" >> /home/facilio/deployment.log
    echo "$(<FACILIO_HOME/deployment-props/service-stage.yml)" >> /home/facilio/deployment.log
    echo "===============================" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "pre_production2" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    cp $FACILIO_HOME/deployment-props/awsprops-stage2.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-props/log4j-stage.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/deployment-props/service-stage.yml $CONF_DIR/service.yml
    cp $FACILIO_HOME/deployment-props/clientAppConfig-stage.yml $CONF_DIR/clientAppConfig.yml
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    echo "copied service file is =======" >> /home/facilio/deployment.log
    echo "$(<FACILIO_HOME/deployment-props/service-stage.yml)" >> /home/facilio/deployment.log
    echo "===============================" >> /home/facilio/deployment.log
fi


if [ "$DEPLOYMENT_GROUP_NAME" = "production_deployment" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-props/log4j-user.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    cp $FACILIO_HOME/deployment-props/awsprops-user.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-props/executors-production.xml $CONF_DIR/executors.xml
    cp $FACILIO_HOME/deployment-props/instantjobexecutors-production.yml $CONF_DIR/instantjobexecutors.yml
    cp $FACILIO-HOME/deployment-props/agentIntegration-production.properties $CONF_DIR/agentIntegration.properties
    cp $FACILIO_HOME/deployment-props/service-production.yml $CONF_DIR/service.yml
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "production-scheduler" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-props/log4j-scheduler.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/deployment-props/awsprops-scheduler.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-props/executors-production.xml $CONF_DIR/executors.xml
    cp $FACILIO_HOME/deployment-props/instantjobexecutors-production.yml $CONF_DIR/instantjobexecutors.yml
    cp $FACILIO_HOME/deployment-props/service-production.yml $CONF_DIR/service.yml
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "production-scheduler-app" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-props/log4j-scheduler-app.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/deployment-props/awsprops-scheduler-app.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-props/executors-org.xml $CONF_DIR/executors.xml
    cp $FACILIO_HOME/deployment-props/instantjobexecutors-org.yml $CONF_DIR/instantjobexecutors.yml
    cp $FACILIO_HOME/deployment-props/service-production.yml $CONF_DIR/service.yml
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "production-kafka" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-props/log4j-kafka.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/deployment-props/awsprops-kafka.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-props/service-production.yml $CONF_DIR/service.yml
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "ae-production-user" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-props/awsprops-ae-user.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-props/log4j-ae-user.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "ae-production-scheduler" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-props/awsprops-ae-scheduler.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-props/log4j-ae-scheduler.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "sp-production-scheduler" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-props/awsprops-sp-scheduler.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-props/log4j-sp-scheduler.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/setenv-sp.sh $APP_HOME/bin/setenv.sh
    cp $FACILIO_HOME/deployment-props/service-sp.yml $CONF_DIR/service.yml
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "sp-production-kafka" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-props/awsprops-sp-kafka.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-props/log4j-sp-kafka.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/setenv-sp.sh $APP_HOME/bin/setenv.sh
    cp $FACILIO_HOME/deployment-props/service-sp.yml $CONF_DIR/service.yml
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "sp-production-user" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-props/awsprops-sp-user.properties $CONF_DIR/awsprops.properties
	cp $FACILIO_HOME/deployment-props/log4j-sp-user.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/deployment-props/service-sp.yml $CONF_DIR/service.yml
    cp $FACILIO_HOME/setenv-sp.sh $APP_HOME/bin/setenv.sh
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "syd-production-scheduler" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-props/awsprops-syd-scheduler.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-props/executors-production-au.xml $CONF_DIR/executors.xml
    cp $FACILIO_HOME/deployment-props/instantjobexecutors-production-au.yml $CONF_DIR/instantjobexecutors.yml
    cp $FACILIO_HOME/deployment-props/log4j-syd-scheduler.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/deployment-props/service-syd.yml $CONF_DIR/service.yml
    cp $FACILIO_HOME/setenv-syd.sh $APP_HOME/bin/setenv.sh
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "syd-production-user" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-props/awsprops-syd-user.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-props/executors-production-au.xml $CONF_DIR/executors.xml
    cp $FACILIO_HOME/deployment-props/instantjobexecutors-production-au.yml $CONF_DIR/instantjobexecutors.yml
    cp $FACILIO_HOME/deployment-props/log4j-syd-user.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/deployment-props/service-syd.yml $CONF_DIR/service.yml
    cp $FACILIO_HOME/setenv-syd.sh $APP_HOME/bin/setenv.sh
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "syd-production-user-critical" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-props/awsprops-syd-user-critical.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-props/executors-org-au.xml $CONF_DIR/executors.xml
    cp $FACILIO_HOME/deployment-props/instantjobexecutors-org-au.yml $CONF_DIR/instantjobexecutors.yml
    cp $FACILIO_HOME/deployment-props/log4j-syd-user-critical.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/deployment-props/service-syd-critical.yml $CONF_DIR/service.yml
    cp $FACILIO_HOME/setenv-syd.sh $APP_HOME/bin/setenv.sh
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "syd-production-scheduler-critical" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-props/awsprops-syd-scheduler-critical.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-props/executors-org-au.xml $CONF_DIR/executors.xml
    cp $FACILIO_HOME/deployment-props/instantjobexecutors-org-au.yml $CONF_DIR/instantjobexecutors.yml
    cp $FACILIO_HOME/deployment-props/log4j-syd-scheduler-critical.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/deployment-props/service-syd-critical.yml $CONF_DIR/service.yml
    cp $FACILIO_HOME/setenv-syd.sh $APP_HOME/bin/setenv.sh
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "eu-production-scheduler" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-props/awsprops-ff-scheduler.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-props/log4j-ff-scheduler.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi


if [ "$DEPLOYMENT_GROUP_NAME" = "eu-production-kafka" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-props/awsprops-ff-kafka.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-props/log4j-ff-kafka.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "eu-production-user" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-props/awsprops-ff-user.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-props/log4j-ff-user.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "demo-user" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-props/log4j-user.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    cp $FACILIO_HOME/deployment-props/awsprops-demo.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-props/service-production.yml $CONF_DIR/service.yml
    cp $FACILIO-HOME/deployment-props/agentIntegration-production.properties $CONF_DIR/agentIntegration.properties
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi


if [ "$DEPLOYMENT_GROUP_NAME" = "uk-production-scheduler" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-props/awsprops-uk-scheduler.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-props/log4j-uk-scheduler.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/deployment-props/service-uk.yml $CONF_DIR/service.yml
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "uk-production-user" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-props/awsprops-uk-user.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-props/log4j-uk-user.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/deployment-props/service-uk.yml $CONF_DIR/service.yml
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "uk-production-kafka" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-props/awsprops-uk-kafka.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-props/log4j-uk-kafka.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/deployment-props/service-uk.yml $CONF_DIR/service.yml
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi