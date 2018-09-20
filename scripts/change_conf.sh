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
    cp $META_INF_DIR/context-stage.xml $META_INF_DIR/context.xml
    cp $UBUNTU_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    sed -i -e 's/localhost:9090/app.facilio.in/g' $CONF_DIR/awsprops.properties
    sed -i -e 's/localhost:8080/facilio.in/g' $CONF_DIR/awsprops.properties
    sed -i'' "s%environment=.*%environment=stage%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%chargebee.site=.*%chargebee.site=payfacilio-test%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%chargebee.api=.*%chargebee.api=test_AcdMBlnceZzwYhGeAX6dkxzocvglIkJjL%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%kinesisServer=.*%kinesisServer=true%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%schedulerServer=.*%schedulerServer=true%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%enable.transaction=.*%enable.transaction=true%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%enable.kinesis=.*%enable.kinesis=true%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%s3.bucket.name=.*%s3.bucket.name=facilio-stage1-data%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%enableeventjob=.*%enableeventjob=false%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%redis.enabled=.*%redis.enabled=true%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%redis.db=.*%redis.db=1%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%servername=.*%servername=stage-330328973.us-west-2.elb.amazonaws.com%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%api.servername=.*%api.servername=api.facilio.in%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%log4j.appender.graylog2.facility=.*%log4j.appender.graylog2.facility=stage%g" $CLASSES_DIR/log4j.properties
    sed -i'' "s%log4j.appender.graylog2.graylogHost=.*%log4j.appender.graylog2.graylogHost=172.31.35.38%g" $CLASSES_DIR/log4j.properties
    sed -i'' "s%websocket.url=.*%websocket.url=wss://api.facilio.in/websocket%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%static.url=.*%static.url=https://static.facilio.in%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%cors.allowed.origins=.*%cors.allowed.origins=http://localhost:8080,http://localhost:9090,https://facilio.in%g" $CONF_DIR/awsprops.properties
    sed -i -e 's/localhost:7444/172.31.10.148:7444/g' $CONF_DIR/awsprops.properties
    sed -i -e 's/tmp/home\/ubuntu\/analytics\/temp/g' $CONF_DIR/awsprops.properties
    sed -i -e 's/anomalyRefreshWaitTimeInSeconds=10/anomalyRefreshWaitTimeInSeconds=1/g' $CONF_DIR/awsprops.properties
    sed -i -e 's/anomalyDetectWaitTimeInSeconds=3/anomalyDetectWaitTimeInSeconds=0/g' $CONF_DIR/awsprops.properties 
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/ubuntu/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "production_deployment" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/ubuntu/deployment.log
    cp $META_INF_DIR/context-production.xml $META_INF_DIR/context.xml
    cp $UBUNTU_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    sed -i'' "s%schedulerServer=.*%schedulerServer=false%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%chargebee.site=.*%chargebee.site=payfacilio%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%chargebee.api=.*%chargebee.api=live_JcuJ3da1RxcdRkVVZsGi1fWquFFMXLsAtW%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%kinesisServer=.*%kinesisServer=false%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%enable.transaction=.*%enable.transaction=true%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%enable.kinesis=.*%enable.kinesis=false%g" $CONF_DIR/awsprops.properties
    sed -i -e 's/localhost:9090/app.facilio.ae/g' $CONF_DIR/awsprops.properties
    sed -i -e 's/localhost:8080/facilio.ae/g' $CONF_DIR/awsprops.properties
    sed -i'' "s%environment=.*%environment=production%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%redis.enabled=.*%redis.enabled=true%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%redis.db=.*%redis.db=2%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%servername=.*%servername=user-production-1756879720.us-west-2.elb.amazonaws.com%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%s3.bucket.name=.*%s3.bucket.name=facilio-ae-data%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%api.servername=.*%api.servername=api.facilio.ae%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%static.url=.*%static.url=https://static.facilio.com%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%websocket.url=.*%websocket.url=wss://api.facilio.ae/websocket%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%log4j.appender.graylog2.facility=.*%log4j.appender.graylog2.facility=production-user%g" $CLASSES_DIR/log4j.properties
    sed -i'' "s%log4j.appender.graylog2.graylogHost=.*%log4j.appender.graylog2.graylogHost=172.31.35.38%g" $CLASSES_DIR/log4j.properties
    sed -i'' "s%cors.allowed.origins=.*%cors.allowed.origins=https://facilio.ae,https://fazilio.com,https://facilio.com,https://facilio.in,https://facilstack.com,https://facilioportal.com,https://wiproenergy.com%g" $CONF_DIR/awsprops.properties
    sed -i -e 's/localhost:7444/172.31.0.68:7444/g' $CONF_DIR/awsprops.properties
    sed -i -e 's/tmp/home\/ubuntu\/analytics\/temp/g' $CONF_DIR/awsprops.properties
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/ubuntu/deployment.log
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "production-scheduler" ]; then
    echo "copying $DEPLOYMENT_GROUP_NAME context file" >> /home/ubuntu/deployment.log
    cp $META_INF_DIR/context-production.xml $META_INF_DIR/context.xml
    cp $UBUNTU_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    sed -i -e 's/localhost:9090/app.facilio.ae/g' $CONF_DIR/awsprops.properties
    sed -i -e 's/localhost:8080/facilio.ae/g' $CONF_DIR/awsprops.properties
    sed -i'' "s%schedulerServer=.*%schedulerServer=true%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%kinesisServer=.*%kinesisServer=true%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%enable.kinesis=.*%enable.kinesis=true%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%environment=.*%environment=production%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%redis.enabled=.*%redis.enabled=true%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%redis.db=.*%redis.db=2%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%servername=.*%servername=user-production-1756879720.us-west-2.elb.amazonaws.com%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%api.servername=.*%api.servername=api.facilio.ae%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%static.url=.*%static.url=https://static.facilio.com%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%websocket.url=.*%websocket.url=wss://api.facilio.ae/websocket%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%cors.allowed.origins=.*%cors.allowed.origins=https://facilio.ae,https://fazilio.com,https://facilio.com,https://facilio.in,https://facilstack.com,https://facilioportal.com%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%s3.bucket.name=.*%s3.bucket.name=facilio-ae-data%g" $CONF_DIR/awsprops.properties
    sed -i'' "s%log4j.appender.graylog2.facility=.*%log4j.appender.graylog2.facility=production-scheduler%g" $CLASSES_DIR/log4j.properties
    sed -i'' "s%log4j.appender.graylog2.graylogHost=.*%log4j.appender.graylog2.graylogHost=172.31.35.38%g" $CLASSES_DIR/log4j.properties
    sed -i -e 's/localhost:7444/172.31.0.68:7444/g' $CONF_DIR/awsprops.properties
    sed -i -e 's/tmp/home\/ubuntu\/analytics\/temp/g' $CONF_DIR/awsprops.properties
    sed -i -e 's/stage\/anomaly/prod\/anomaly/g' $CONF_DIR/awsprops.properties
    echo "copied $DEPLOYMENT_GROUP_NAME context file" >> /home/ubuntu/deployment.log
fi