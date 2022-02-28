#!/usr/bin/env bash
today=`date  +%F`
tnow=`date +%T`
dt=$today" "$tnow

pid=`/home/facilio/jdk/bin/jps | grep Bootstrap| cut -d' ' -f1`
if [ -z "$pid" ]; then
    echo "Tomcat is not running $dt"
    sh start_server.sh
    exit 0;
else
    echo "Tomat is running $dt"
fi