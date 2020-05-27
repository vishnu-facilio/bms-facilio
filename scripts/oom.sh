#! /bin/bash
export CONF_DIR="/home/facilio/tomcat/webapps/ROOT/WEB-INF/classes/conf"
ipAddress=`hostname -I|awk '{$1=$1};1'`
servername=`grep "app.domain=" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`
environment=`grep "environment" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`
schedulerServer=`grep "schedulerServer" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`
messageProcessor=`grep "messageProcessor" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`
aws ses send-email --region us-west-2 --from oomsh@facilio.com --to devops@facilio.com --subject "OOM Occurred in $environment $ipAddress" --text "scheduler=$schedulerServer  kinesis=$messageProcessor"
nohup sh /home/facilio/start_server.sh &