#!/usr/bin/env bash

xvfb=`ps auxx | grep Xvfb | grep -v "grep"`

if [ "xvfb$xvfb" = "xvfb" ]; then
    export DISPLAY=:99
    Xvfb $DISPLAY -screen 0 1024x768x16 &
fi