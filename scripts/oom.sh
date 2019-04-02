#! /bin/bash
export CONF_DIR="/home/ubuntu/tomcat/webapps/ROOT/WEB-INF/classes/conf"
ipAddress=`hostname -I|awk '{$1=$1};1'`
servername=`grep "app.domain" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`
environment=`grep "environment" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`
schedulerServer=`grep "schedulerServer" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`
messageProcessor=`grep "messageProcessor" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`
#echo "scheduler=$schedulerServer  kinesis=$messageProcessor" | mail -s "OOM Occurred in $environment $ipAddress" alerts@facilio.com
sh /home/ubuntu/start_server.sh