#!/usr/bin/env bash
ipAddress=`hostname -I|awk '{$1=$1};1'`
lastusage=`cat disk_avail.txt`
currentvalue=`du tomcat/logs | awk '{ print $1 }'`
dusage=$(df -Ph | grep '/dev/nvme0n1p1' | sed s/%//g | awk '{ if($5 > 90) print $0;}')
usedper=$(df -Ph | grep '/dev/nvme0n1p1' | awk '{ print $5}')
available=$(df -Ph | grep '/dev/nvme0n1p1' | awk '{ print $4}')
standardvalue=1500000
used=$(df -Ph | grep '/dev/nvme0n1p1' | awk '{ print $3}')
cal=`expr "$currentvalue" - "$lastusage"`
fscount=$(echo "$dusage" | wc -l)

echo "Last 3hours logs size = $cal kb"

if [ "$cal" -gt "$standardvalue" ];
then
    echo "Disk usage is over threshold"
    aws ses send-email --region us-west-2 --from alerts@facilio.com --to devops@facilio.com --subject "Logs Alert on $ipAddress Region = US-WEST-2" --text "logs Alert  - logs flooded in $ipAddress …logs are writing more than 1500mb in last 3 hours Region = US-WEST-2"
else
    echo "last 3 hours logs size = $cal kb ..."
fi

if [ "$fscount" -ge 2 ]; then
    echo "$dusage" | aws ses send-email --region us-west-2 --from alerts@facilio.com --to devops@facilio.com --subject "Disk space Alert on $ipAddress" --text "Disk Space Alert  - $usedper of disk space has been used IP = $ipAddress AvailableSpace =$available Used = $used ($usedper)"
    sh move_logs.sh
else
    echo "Disk usage is in under threshold"
fi