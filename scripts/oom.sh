#! /bin/bash
export CONF_DIR="/home/facilio/tomcat/webapps/ROOT/WEB-INF/classes/conf"
ipAddress=`hostname -I|awk '{$1=$1};1'`
servername=`grep "app.domain=" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`
environment=`grep "environment=" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`
schedulerServer=`grep "schedulerServer=" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`
messageProcessor=`grep "messageProcessor=" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`
region_name=`grep "region=" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`
region_name=`echo $region_name | cut -d ' ' -f1`
case $region_name in
        'us-west-2')
                region_name="US"
                ;;
        'eu-central-1')
                region_name="EU - Frankfurt"
                ;;
        'ap-southeast-2')
                region_name="AU - Australia"
                ;;
        'ap-southeast-1')
                region_name="SG - Singapore"
                ;;
        'eu-west-2')
                region_name="UK - London"
                ;;
        *)
                region_name="US"
esac
aws ses send-email --region us-west-2 --from oomsh@facilio.com --to devops@facilio.com --subject "OOM Occurred in $environment $ipAddress" --text "scheduler=$schedulerServer  kinesis=$messageProcessor region=$region_name"
nohup sh /home/facilio/move_hprof.sh &
nohup sh /home/facilio/start_server.sh &
