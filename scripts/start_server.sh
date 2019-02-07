#!/usr/bin/env bash
echo "starting the server..."
export UBUNTU_HOME="/home/ubuntu"
export APP_HOME="$UBUNTU_HOME/tomcat"
export CONF_DIR="$APP_HOME/webapps/ROOT/WEB-INF/classes/conf"

cd /home/ubuntu

sudo killall -9 java
sudo sh $APP_HOME/bin/catalina.sh start
sudo chmod 644 $APP_HOME/logs/*
sudo rm -rf $UBUNTU_HOME/deployment-files/*

echo "server started..."