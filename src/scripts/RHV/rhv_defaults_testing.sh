#!/bin/bash
source ./vars.sh
# Disable the Python warnings
export PYTHONWARNINGS="ignore:Unverified HTTPS request"

if [ "${PROVISION_STATUS}" == 0 ]; then
    echo "Succeed to provison beaker system!"
    pushd entitlement-tests/
    python CDN/RHV/rhv_analyze.py case
    PREPARE_STATUS=$?
    echo "export PREPARE_STATUS=${PREPARE_STATUS}" >> ../vars.sh

    if [ ${PREPARE_STATUS} -eq 0 ]; then
        # export System_IP
        . ip.sh

        # Run tests with nosetests
        nosetests CDN/Tests/*.py --with-xunit --nocapture --xunit-file=../nosetests_original.xml
        TEST_STATUS=$?
        popd

        if [ ${TEST_STATUS} -ne 0 ]; then
            echo "ERROR: Failed to do testing!"
            exit 1
        fi
    else
        echo "ERROR: Failed to prepare testing!"
        popd
        exit 1
    fi
else
    echo "ERROR: Failed to provision beaker system!"
    exit 1
fi
