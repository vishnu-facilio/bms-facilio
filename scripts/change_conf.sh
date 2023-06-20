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
    cp $FACILIO_HOME/deployment-files/service-stage.yml $CONF_DIR/service.yml
    cp $FACILIO_HOME/deployment-files/clientAppConfig-stage.yml $CONF_DIR/clientAppConfig.yml
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    echo "copied service file is =======" >> /home/facilio/deployment.log
    echo "$(<FACILIO_HOME/deployment-files/service-stage.yml)" >> /home/facilio/deployment.log
    echo "===============================" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "iot-stage" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    cp $FACILIO_HOME/deployment-files/awsprops-iot.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-files/log4j-stage.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/deployment-files/service-stage.yml $CONF_DIR/service.yml
    cp $FACILIO_HOME/deployment-files/clientAppConfig-stage.yml $CONF_DIR/clientAppConfig.yml
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    echo "copied service file is =======" >> /home/facilio/deployment.log
    echo "$(<FACILIO_HOME/deployment-files/service-stage.yml)" >> /home/facilio/deployment.log
    echo "===============================" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "testing-stage" ] || [ "$DEPLOYMENT_GROUP_NAME" = "cafm-stage" ] || [ "$DEPLOYMENT_GROUP_NAME" = "platform-stage" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    cp $FACILIO_HOME/deployment-files/awsprops-team.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-files/log4j-stage.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/deployment-files/service-stage.yml $CONF_DIR/service.yml
    cp $FACILIO_HOME/deployment-files/clientAppConfig-stage.yml $CONF_DIR/clientAppConfig.yml
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    echo "copied service file is =======" >> /home/facilio/deployment.log
    echo "$(<FACILIO_HOME/deployment-files/service-stage.yml)" >> /home/facilio/deployment.log
    echo "===============================" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "pre_production2" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    cp $FACILIO_HOME/deployment-files/awsprops-stage2.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-files/log4j-stage2.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/deployment-files/service-stage.yml $CONF_DIR/service.yml
    cp $FACILIO_HOME/deployment-files/clientAppConfig-stage.yml $CONF_DIR/clientAppConfig.yml
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    echo "copied service file is =======" >> /home/facilio/deployment.log
    echo "$(<FACILIO_HOME/deployment-files/service-stage.yml)" >> /home/facilio/deployment.log
    echo "===============================" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "stage2-preprod" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    cp $FACILIO_HOME/deployment-files/awsprops-preprod-stage2.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-files/log4j-stage2.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/deployment-files/service-stage.yml $CONF_DIR/service.yml
    cp $FACILIO_HOME/deployment-files/clientAppConfig-stage.yml $CONF_DIR/clientAppConfig.yml
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    echo "copied service file is =======" >> /home/facilio/deployment.log
    echo "$(<FACILIO_HOME/deployment-files/service-stage.yml)" >> /home/facilio/deployment.log
    echo "===============================" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "stage-dispatcher" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    cp $FACILIO_HOME/deployment-files/awsprops-stage-dispatcher.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-files/log4j-stage-dispatcher.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/deployment-files/service-stage.yml $CONF_DIR/service.yml
    cp $FACILIO_HOME/deployment-files/clientAppConfig-stage.yml $CONF_DIR/clientAppConfig.yml
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    echo "copied service file is =======" >> /home/facilio/deployment.log
    echo "$(<FACILIO_HOME/deployment-files/service-stage.yml)" >> /home/facilio/deployment.log
    echo "===============================" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "production_deployment" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-files/log4j-user.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    cp $FACILIO_HOME/deployment-files/awsprops-user.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-files/executors-production.xml $CONF_DIR/executors.xml
    cp $FACILIO_HOME/deployment-files/instantjobexecutors-production.yml $CONF_DIR/instantjobexecutors.yml
    cp $FACILIO-HOME/deployment-files/agentIntegration-production.properties $CONF_DIR/agentIntegration.properties
    cp $FACILIO_HOME/deployment-files/service-production.yml $CONF_DIR/service.yml
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "preprod" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-files/log4j-preprod.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    cp $FACILIO_HOME/deployment-files/awsprops-preprod.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-files/executors-production.xml $CONF_DIR/executors.xml
    cp $FACILIO_HOME/deployment-files/instantjobexecutors-production.yml $CONF_DIR/instantjobexecutors.yml
    cp $FACILIO-HOME/deployment-files/agentIntegration-production.properties $CONF_DIR/agentIntegration.properties
    cp $FACILIO_HOME/deployment-files/service-production.yml $CONF_DIR/service.yml
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "preprod-scheduler" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-files/log4j-preprod-scheduler.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    cp $FACILIO_HOME/deployment-files/awsprops-preprod-scheduler.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-files/executors-production.xml $CONF_DIR/executors.xml
    cp $FACILIO_HOME/deployment-files/instantjobexecutors-production.yml $CONF_DIR/instantjobexecutors.yml
    cp $FACILIO-HOME/deployment-files/agentIntegration-production.properties $CONF_DIR/agentIntegration.properties
    cp $FACILIO_HOME/deployment-files/service-production.yml $CONF_DIR/service.yml
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "production-scheduler" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-files/log4j-scheduler.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/deployment-files/awsprops-scheduler.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-files/executors-production.xml $CONF_DIR/executors.xml
    cp $FACILIO_HOME/deployment-files/instantjobexecutors-production.yml $CONF_DIR/instantjobexecutors.yml
    cp $FACILIO_HOME/deployment-files/service-production.yml $CONF_DIR/service.yml
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "production-kafka" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-files/log4j-kafka.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/deployment-files/awsprops-kafka.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-files/service-production.yml $CONF_DIR/service.yml
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "ae-production-user" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-files/awsprops-ae-user.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-files/log4j-ae-user.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "ae-preprod" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-files/awsprops-ae-preprod.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-files/log4j-ae-preprod.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "ae-preprod-scheduler" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-files/awsprops-ae-preprod-scheduler.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-files/log4j-ae-preprod-scheduler.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "ae-production-scheduler" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-files/awsprops-ae-scheduler.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-files/log4j-ae-scheduler.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "ae-production-kafka" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-files/awsprops-ae-kafka.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-files/log4j-ae-kafka.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "sp-production-scheduler" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-files/awsprops-sp-scheduler.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-files/log4j-sp-scheduler.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/setenv-sp.sh $APP_HOME/bin/setenv.sh
    cp $FACILIO_HOME/deployment-files/service-sp.yml $CONF_DIR/service.yml
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "sp-production-kafka" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-files/awsprops-sp-kafka.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-files/log4j-sp-kafka.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/setenv-sp.sh $APP_HOME/bin/setenv.sh
    cp $FACILIO_HOME/deployment-files/service-sp.yml $CONF_DIR/service.yml
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "sp-production-user" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-files/awsprops-sp-user.properties $CONF_DIR/awsprops.properties
	cp $FACILIO_HOME/deployment-files/log4j-sp-user.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/deployment-files/service-sp.yml $CONF_DIR/service.yml
    cp $FACILIO_HOME/setenv-sp.sh $APP_HOME/bin/setenv.sh
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "sp-preprod" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-files/awsprops-sp-preprod.properties $CONF_DIR/awsprops.properties
	cp $FACILIO_HOME/deployment-files/log4j-sp-preprod.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/deployment-files/service-sp.yml $CONF_DIR/service.yml
    cp $FACILIO_HOME/setenv-sp.sh $APP_HOME/bin/setenv.sh
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "sp-preprod-scheduler" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-files/awsprops-sp-preprod-scheduler.properties $CONF_DIR/awsprops.properties
	cp $FACILIO_HOME/deployment-files/log4j-sp-preprod-scheduler.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/deployment-files/service-sp.yml $CONF_DIR/service.yml
    cp $FACILIO_HOME/setenv-sp.sh $APP_HOME/bin/setenv.sh
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "syd-kafka" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-files/awsprops-syd-kafka.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-files/log4j-syd-kafka.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/setenv-syd.sh $APP_HOME/bin/setenv.sh
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "ae-kafka" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-files/awsprops-ae-kafka.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-files/log4j-ae-kafka.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "syd-production-user-critical" ]; then
   echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
       cp $FACILIO_HOME/deployment-files/awsprops-syd-user.properties $CONF_DIR/awsprops.properties
       cp $FACILIO_HOME/deployment-files/executors-production-au.xml $CONF_DIR/executors.xml
       cp $FACILIO_HOME/deployment-files/instantjobexecutors-production-au.yml $CONF_DIR/instantjobexecutors.yml
       cp $FACILIO_HOME/deployment-files/log4j-syd-user.properties $CLASSES_DIR/log4j.properties
       cp $FACILIO_HOME/deployment-files/service-syd.yml $CONF_DIR/service.yml
       cp $FACILIO_HOME/setenv-syd.sh $APP_HOME/bin/setenv.sh
       echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "syd-preprod" ]; then
   echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
       cp $FACILIO_HOME/deployment-files/awsprops-syd-preprod.properties $CONF_DIR/awsprops.properties
       cp $FACILIO_HOME/deployment-files/executors-production-au.xml $CONF_DIR/executors.xml
       cp $FACILIO_HOME/deployment-files/instantjobexecutors-production-au.yml $CONF_DIR/instantjobexecutors.yml
       cp $FACILIO_HOME/deployment-files/log4j-syd-preprod.properties $CLASSES_DIR/log4j.properties
       cp $FACILIO_HOME/deployment-files/service-syd.yml $CONF_DIR/service.yml
       cp $FACILIO_HOME/setenv-syd.sh $APP_HOME/bin/setenv.sh
       echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "syd-preprod-scheduler" ]; then
   echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
       cp $FACILIO_HOME/deployment-files/awsprops-syd-preprod-scheduler.properties $CONF_DIR/awsprops.properties
       cp $FACILIO_HOME/deployment-files/executors-production-au.xml $CONF_DIR/executors.xml
       cp $FACILIO_HOME/deployment-files/instantjobexecutors-production-au.yml $CONF_DIR/instantjobexecutors.yml
       cp $FACILIO_HOME/deployment-files/log4j-syd-preprod-scheduler.properties $CLASSES_DIR/log4j.properties
       cp $FACILIO_HOME/deployment-files/service-syd.yml $CONF_DIR/service.yml
       cp $FACILIO_HOME/setenv-syd.sh $APP_HOME/bin/setenv.sh
       echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "syd-production-scheduler-critical" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
        cp $FACILIO_HOME/deployment-files/awsprops-syd-scheduler.properties $CONF_DIR/awsprops.properties
        cp $FACILIO_HOME/deployment-files/executors-production-au.xml $CONF_DIR/executors.xml
        cp $FACILIO_HOME/deployment-files/instantjobexecutors-production-au.yml $CONF_DIR/instantjobexecutors.yml
        cp $FACILIO_HOME/deployment-files/log4j-syd-scheduler.properties $CLASSES_DIR/log4j.properties
        cp $FACILIO_HOME/deployment-files/service-syd.yml $CONF_DIR/service.yml
        cp $FACILIO_HOME/setenv-syd.sh $APP_HOME/bin/setenv.sh
        echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "uk-production-scheduler" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-files/awsprops-uk-scheduler.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-files/log4j-uk-scheduler.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/deployment-files/service-uk.yml $CONF_DIR/service.yml
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "uk-production-user" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-files/awsprops-uk-user.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-files/log4j-uk-user.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/deployment-files/service-uk.yml $CONF_DIR/service.yml
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "uk-preprod" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-files/awsprops-uk-preprod.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-files/log4j-uk-preprod.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/deployment-files/service-uk.yml $CONF_DIR/service.yml
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "uk-preprod-scheduler" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-files/awsprops-uk-preprod-scheduler.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-files/log4j-uk-preprod-scheduler.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/deployment-files/service-uk.yml $CONF_DIR/service.yml
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "uk-production-kafka" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-files/awsprops-uk-kafka.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-files/log4j-uk-kafka.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/deployment-files/service-uk.yml $CONF_DIR/service.yml
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "azure-test-production-scheduler" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    cp $FACILIO_HOME/deployment-files/log4j-azure-test-scheduler.properties $CLASSES_DIR/log4j.properties    
    cp $FACILIO_HOME/deployment-files/awsprops-azure-test-scheduler.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-files/imsTopicInfo-azure.yml $CONF_DIR/fms/imsTopicInfo.yml
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    echo "copied service file is =======" >> /home/facilio/deployment.log
    echo "$(<FACILIO_HOME/deployment-files/service-stage.yml)" >> /home/facilio/deployment.log
    echo "===============================" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "azure-deployment" ] || [ "$DEPLOYMENT_GROUP_NAME" = "azure-test-production-user" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    cp $FACILIO_HOME/deployment-files/log4j-azure-test-user.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/deployment-files/clientAppConfig-stage.yml $CONF_DIR/clientAppConfig.yml
    cp $FACILIO_HOME/deployment-files/awsprops-azure-test-user.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-files/imsTopicInfo-azure.yml $CONF_DIR/fms/imsTopicInfo.yml
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    echo "copied service file is =======" >> /home/facilio/deployment.log
    echo "$(<FACILIO_HOME/deployment-files/service-stage.yml)" >> /home/facilio/deployment.log
    echo "===============================" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "azure-deployment" ] || [ "$DEPLOYMENT_GROUP_NAME" = "azure-production-user" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    cp $FACILIO_HOME/deployment-files/log4j-azure-user.properties $CLASSES_DIR/log4j.properties                
    cp $FACILIO_HOME/deployment-files/awsprops-azure-user.properties $CONF_DIR/awsprops.properties
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    echo "copied service file is =======" >> /home/facilio/deployment.log
    echo "$(<FACILIO_HOME/deployment-files/service-stage.yml)" >> /home/facilio/deployment.log
    echo "===============================" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "azure-production-schedule" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    cp $FACILIO_HOME/deployment-files/log4j-azure-scheduler.properties $CLASSES_DIR/log4j.properties                
    cp $FACILIO_HOME/deployment-files/awsprops-azure-scheduler.properties $CONF_DIR/awsprops.properties
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    echo "copied service file is =======" >> /home/facilio/deployment.log
    echo "$(<FACILIO_HOME/deployment-files/service-stage.yml)" >> /home/facilio/deployment.log
    echo "===============================" >> /home/facilio/deployment.log
fi


if [ "$DEPLOYMENT_GROUP_NAME" = "azure-production-processor" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    cp $FACILIO_HOME/deployment-files/log4j-azure-processor.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/deployment-files/awsprops-azure-processor.properties $CONF_DIR/awsprops.properties
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    echo "copied service file is =======" >> /home/facilio/deployment.log
    echo "$(<FACILIO_HOME/deployment-files/service-stage.yml)" >> /home/facilio/deployment.log
    echo "===============================" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "stage-scheduler-uk" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-files/awsprops-stage-scheduler-uk.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-files/clientAppConfig-stage.yml $CONF_DIR/clientAppConfig.yml
    cp $FACILIO_HOME/deployment-files/log4j-stage-scheduler-uk.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/deployment-files/service-uk.yml $CONF_DIR/service.yml
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "stage-user-uk" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
    cp $FACILIO_HOME/deployment-files/awsprops-stage-user-uk.properties $CONF_DIR/awsprops.properties
    cp $FACILIO_HOME/deployment-files/clientAppConfig-stage.yml $CONF_DIR/clientAppConfig.yml
    cp $FACILIO_HOME/deployment-files/log4j-stage-user-uk.properties $CLASSES_DIR/log4j.properties
    cp $FACILIO_HOME/deployment-files/service-uk.yml $CONF_DIR/service.yml
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/facilio/deployment.log
fi