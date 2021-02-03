#!/bin/bash
# Disable the Python warnings
export PYTHONWARNINGS="ignore:Unverified HTTPS request"

# Anayze and then trigger downstream jobs
pushd entitlement-tests/
python ./CDN/RHV/rhv_analyze.py manual
popd
cp -r entitlement-tests/*.dir .

for i in `ls -d *.dir`; do echo $i >> properties.check; done
cat properties.check
