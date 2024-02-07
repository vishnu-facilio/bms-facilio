#!/bin/bash

if [ "x$PWD" = "x" ]; then
	exit 1;
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "pre_production" ]; then
	cd /tmp/bmsclient/dist/
	aws s3 cp . s3://app.facilio.in --recursive --acl public-read
	aws configure set preview.cloudfront true
	aws cloudfront create-invalidation --distribution-id 'E2VXRWBORZUC0Y' --paths '/*'
	echo "Copied files to app.facilio.in"
fi

if [ "$DEPLOYMENT_GROUP_NAME" = "production_deployment" ]; then
	cd /tmp/bmsclient/dist/
	aws s3 cp . s3://app.fazilio.com --recursive --acl public-read
	aws configure set preview.cloudfront true
	aws cloudfront create-invalidation --distribution-id 'E1WFAOP7J0XF7B' --paths '/*'
	echo "Copied files to app.facilio.com"
fi

rm -rf  /tmp/bmsclient
