#!/bin/bash
source ./vars.sh

# Modify PinFile to provision suitable Beaker system
if [ "${Variant}" == "RHVH" ]; then
  PinFileDir=./entitlement-tests/CCI/Linchpin/beaker/RHVH
  OutputFile=${PinFileDir}/resources/rhvh.output
else
  PinFileDir=./entitlement-tests/CCI/Linchpin/beaker/simple
  OutputFile=${PinFileDir}/resources/simple.output
fi
PinFile=${PinFileDir}/PinFile
sed -i -e "s/DISTRO/${Distro}/g" -e "s/ARCH/${Arch}/g" -e "s/VARIANT/${Variant}/g" ${PinFile}
cat ${PinFile}

# Provision testing system in Beaker
source /home/jenkins/.venv/bin/activate
linchpin -w ${PinFileDir} validate
linchpin -w ${PinFileDir} -vvvv up
PROVISION_STATUS=$?
deactivate
ls -lR ./entitlement-tests/CCI/Linchpin/beaker/

if [ "${PROVISION_STATUS}" == 0 ]; then
  cat ${OutputFile}
  Testing_System=`python -c """import json; print json.load(open('${OutputFile}'))[0]['system']"""`
  echo EXISTING_NODES=${Testing_System} > ./RESOURCES.txt
fi

echo "export PROVISION_STATUS=${PROVISION_STATUS}" >> ./vars.sh