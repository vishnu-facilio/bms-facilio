#!/usr/bin/env bash
echo "stopping the hydra..."
pid=`lsof -t -i:4444`

if [ -z "$pid" ]; then
   echo "Hydra not running.."
   exit 0;
fi

kill $(lsof -t -i:4444)
echo "Hydra Stopped.."
