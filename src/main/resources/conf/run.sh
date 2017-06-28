#!/bin/bash

# Functions ==============================================

# return 1 if global command line program installed, else 0
function brew_is_installed {
	a=$(brew -v);
	local return_=0
	if [ ${#a} -gt 0 ]; then
    	local return_=1;
	else
		local return_=0;
	fi
  	echo "$return_"
}

# return 1 if global command line program installed, else 0
function node_is_installed {
	a=$(node -v);
	local return_=0
	if [ ${#a} -gt 0 ]; then
    	local return_=1;
	else
		local return_=0;
	fi
  	echo "$return_"
}

# return 1 if local npm package is installed at ./node_modules, else 0
# example
# echo "bacstack : $(npm_package_is_installed bacstack)"
function npm_package_is_installed {
  # set to 1 initially
  local return_=1
  # set to 0 if not found
  ls node_modules | grep $1 >/dev/null 2>&1 || { local return_=0; }
  # return value
  echo "$return_"
}

# ============================================== Functions

# command line programs
if [ $(brew_is_installed) == 1 ]; then
    echo "brew already installed"
else
	echo "installing brew..."
    ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
fi

# command line programs
if [ $(node_is_installed) == 1 ]; then
    echo "node already installed"
else
	echo "installing node..."
    brew install node
fi

# local npm packages
if [ $(npm_package_is_installed bacstack) == 1 ]; then
    echo "bacstack already installed"
else
	echo "bacstack not exist"
	echo "installing bacstack..."
    npm install --save bacstack
fi

# local npm packages
if [ $(npm_package_is_installed aws-sdk) == 1 ]; then
    echo "aws-sdk already installed"
else
	echo "aws-sdk not exist"
	echo "installing aws-sdk..."
    npm install aws-sdk
fi

# local npm packages
if [ $(npm_package_is_installed aws-iot-device-sdk) == 1 ]; then
    echo "aws-iot-device-sdk already installed"
else
	echo "aws-iot-device-sdk not exist"
	echo "installing aws-iot-device-sdk..."
    npm install aws-iot-device-sdk
fi

# local npm packages
if [ $(npm_package_is_installed jsonfile) == 1 ]; then
    echo "jsonfile already installed"
else
	echo "jsonfile not exist"
	echo "installing jsonfile..."
    npm install --save jsonfile
fi

# local npm packages
if [ $(npm_package_is_installed node-cron) == 1 ]; then
    echo "node-cron already installed"
else
	echo "node-cron not exist"
	echo "installing node-cron..."
    npm install --save node-cron
fi

# local npm packages
if [ $(npm_package_is_installed fs) == 1 ]; then
    echo "fs already installed"
else
	echo "fs not exist"
	echo "installing fs..."
    npm install --save fs
fi

node deviceconnector.js