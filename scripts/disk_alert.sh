#!/usr/bin/env bash

export CONF_DIR="/home/facilio/tomcat/webapps/ROOT/WEB-INF/classes/conf"
ipAddress=`hostname -I|awk '{$1=$1};1'`
lastusage=`cat disk_avail.txt`
currentvalue=`du tomcat/logs | awk '{ print $1 }'`
dusage=$(df -Ph | grep '/dev/nvme0n1p1' | sed s/%//g | awk '{ if($5 > 90) print $0;}' | wc -l)
usedper=$(df -Ph | grep '/dev/nvme0n1p1' | awk '{ print $5}')
available=$(df -Ph | grep '/dev/nvme0n1p1' | awk '{ print $4}')
standardvalue=1500000
used=$(df -Ph | grep '/dev/nvme0n1p1' | awk '{ print $3}')
cal=`expr "$currentvalue" - "$lastusage"`
region_name=`grep "\bregion=" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`

echo "Last 3hours logs size = $cal kb"

if [ "$cal" -gt "$standardvalue" ];
then
    echo "Disk usage is over threshold"
    aws ses send-email --region us-west-2 --from alerts@facilio.com --to devops@facilio.com --subject "Logs Alert on $ipAddress Region = US-WEST-2" --text "logs Alert  - logs flooded in $ipAddress .More than 1.5GB of logs have been written in the previous 3 hours. Region = $region_name"
else
    echo "last 3 hours logs size = $cal kb ..."
fi

if [ "$dusage" = "1" ]; then
    echo "$dusage" | aws ses send-email --region us-west-2 --from alerts@facilio.com --to devops@facilio.com --subject "Disk space Alert on $ipAddress" --text "Disk Space Alert  - $usedper of disk space has been used IP = $ipAddress AvailableSpace =$available Used = $used ($usedper)"
    curl -X POST -H "Content-Type: application/json" -d '{
      "recipient":{
        "thread_key":"6859821310705272"
      },
      "messaging_type": "RESPONSE",
      "message":{
        "text":"Disk Space Alert - '$usedper' of disk space has been used \nIP = '$ipAddress' \nRegion = '$region_name' \nAvailableSpace ='$available' \nUsed = '$used' ('$usedper')"
      }
    }' "https://graph.workplace.com/me/messages?access_token=DQVJ1OUE5TVlDZAlMxYlhnTy1qREhHd0p1S0tVTkR1MDMzMFdOd1JudklmbFJMd0dMbW5ObmFGdXZAkRFNHS2RCS2o0V2ZAva2kyOGFzYXotZAWNUZAEtBSFhoSk9ENjVhNGt0OTB1R0RqbTVWTmJiWnZAjYlZADa1VGR1hocWE5eDVuRzBDOEFfdWFzVFphbWtQUWlWSEhrT1BXeWJnZAlRVdF9wSmJpTjU1X1g5LU1Tc2xRZA24tbHE1OGw0SDlfQi1ESW11ZA2p3Q3VB"
    sh move_logs.sh
else
    echo "Disk usage is in under threshold"
fi
