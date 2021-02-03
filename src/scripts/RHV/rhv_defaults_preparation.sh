#!/bin/bash
env | grep "^PID="
if [ $? -eq 0 ]; then
    echo "export PID=${PID}" >> ./vars.sh
fi

if [ -n "${Testing_System}" ]; then
    echo EXISTING_NODES=${Testing_System} > RESOURCES.txt

    # Using the file to transfer variables
    echo "export PROVISION_STATUS=0" >> ./vars.sh
fi
