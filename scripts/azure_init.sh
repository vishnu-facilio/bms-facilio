#!/usr/bin/env bash

export FACILIO_HOME="/home/facilio"
export APP_HOME="$FACILIO_HOME/tomcat"
export BMS_DIR="$APP_HOME/webapps/ROOT"
export CLASSES_DIR="$BMS_DIR/WEB-INF/classes"
export CONF_DIR="$CLASSES_DIR/conf"
export META_INF_DIR="$BMS_DIR/META-INF"
sudo sh $FACILIO_HOME/scripts/kill_server.sh

sudo rm -rf $APP_HOME/webapps/*

sudo mv /home/facilio/target/ROOT.war /home/facilio/tomcat/webapps/
sudo unzip -o $APP_HOME/webapps/ROOT.war -d $APP_HOME/webapps/ROOT

if [ "$1" = "production-user" ]; then
    sudo cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    sudo cp $FACILIO_HOME/deployment-files/awsprops-azure-user.properties $CONF_DIR/awsprops.properties
fi

if [ "$1" = "production-scheduler" ]; then
    cp $FACILIO_HOME/setenv.sh $APP_HOME/bin/setenv.sh
    cp $FACILIO_HOME/deployment-files/awsprops-azure-scheduler.properties $CONF_DIR/awsprops.properties
fi

sudo sh $FACILIO_HOME/scripts/start_server.sh

