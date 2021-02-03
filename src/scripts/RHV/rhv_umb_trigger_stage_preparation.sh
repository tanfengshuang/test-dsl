#!/bin/bash
echo "***** HEADERS *****"
echo $MESSAGE_HEADERS
echo "***** MESSAGE *****"
echo $CI_MESSAGE

# Disable the Python warnings
export PYTHONWARNINGS="ignore:Unverified HTTPS request"

# export testing params
export ERRATA_ID=`python -c "import os,json; CI_MESSAGE = json.loads(os.environ['CI_MESSAGE']); print CI_MESSAGE['errata_id']"`
export errata_status=`python -c "import os,json; CI_MESSAGE = json.loads(os.environ['CI_MESSAGE']); print CI_MESSAGE['errata_status']"`
export release=`python -c "import os,json; CI_MESSAGE = json.loads(os.environ['CI_MESSAGE']); print CI_MESSAGE['release']"`

# Anayze and then trigger downstream jobs
pushd entitlement-tests/
python ./CDN/RHV/rhv_analyze.py umb
popd
cp -r entitlement-tests/*.dir .

for i in `ls -d *.dir`; do echo $i >> properties.check; done
cat properties.check
