#!/usr/bin/env bash

export APP_HOME="/home/facilio/tomcat"
export CONF_DIR="$APP_HOME/webapps/ROOT/WEB-INF/classes/conf"

export isScheduler=`grep "schedulerServer=" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`
export isMessageProcessor=`grep "messageProcessor=" $CONF_DIR/awsprops.properties | cut -d'=' -f 2`

SECRET=$(aws secretsmanager get-secret-value --secret-id prod-hydra-secret | python2 -c "import sys, json; print json.load(sys.stdin)['SecretString']")
export SECRETS_SYSTEM=$(echo $SECRET | python2 -c "import sys, json; print json.load(sys.stdin)['secret']")
export SERVE_ADMIN_HOST=localhost
export SERVE_PUBLIC_HOST=0.0.0.0
SECRET=$(aws secretsmanager get-secret-value --secret-id prod-db-hydra | python2 -c "import sys, json; print json.load(sys.stdin)['SecretString']")
DB_USERNAME=$(echo $SECRET | python2 -c "import sys, json; print json.load(sys.stdin)['username']")
DB_PASSWORD=$(echo $SECRET | python2 -c "import sys, json; print json.load(sys.stdin)['password']")
DB_HOST=$(echo $SECRET | python2 -c "import sys, json; print json.load(sys.stdin)['host']")
DB_PORT=$(echo $SECRET | python2 -c "import sys, json; print json.load(sys.stdin)['port']")
export DSN="mysql://${DB_USERNAME}:${DB_PASSWORD}@tcp(${DB_HOST}:${DB_PORT})/hydra"
export OAUTH2_CLIENT_CREDENTIALS_DEFAULT_GRANT_ALLOWED_SCOPE=true
export SERVE_TLS_ALLOW_TERMINATION_FROM='["127.0.0.1/32","0.0.0.0/32"]'
export SERVE_ADMIN_TLS_ALLOW_TERMINATION_FROM="127.0.0.1/32"
export SERVE_PUBLIC_TLS_ALLOW_TERMINATION_FROM="0.0.0.0/32"
export SERVE_ADMIN_TLS_ENABLED=false

if [ "$isMessageProcessor" = "false" ] && [ "$isScheduler" = "false" ]; then
   nohup ./hydra/hydra serve all --dangerous-force-http &
fi
