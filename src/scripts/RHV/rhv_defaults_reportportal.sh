#!/bin/bash

# copy ReportPortal config file
cp $WORKSPACE/entitlement-tests/CCI/ReportPortal/rhv_rp_conf.json .

# update ReportPortal config file
# 1. set ERRATA_ID in description of launches
errata_url="https://errata.devel.redhat.com/errata/details/${ERRATA_ID}"
errata_url_str="[${ERRATA_ID}](${errata_url})"
sed -i -e "s#ERRATA_ID#${errata_url_str}#g" rhv_rp_conf.json

# 2. set launches name
sed -i -e "s/NAME/${Variant} ${Arch} ${CDN}CDN/g" rhv_rp_conf.json

# 3. set PIDs
# get pids from test case names
if [[ "$PID" == "" ]]; then
    PID=""
    for i in `ls $WORKSPACE/entitlement-tests/CDN/Tests/RHV*.py`
    do
        PID="${PID} ${i:0-6:3},"
    done
fi
sed -i -e "s/_PID/${PID}/g" rhv_rp_conf.json

# 4. Set Polarion run url
export polarion_url_file=`ls $WORKSPACE/nosetests-*`
polarion_url=`python -c """import json,os; r=json.load(open(os.environ['polarion_url_file'])); print(r['testrun-url'] if 'testrun-url' in r.keys() else 'None')"""`
if [[ "${polarion_url}" == "None" ]]; then
    sed -i -e "s/Polarion_URL/None/g" rhv_rp_conf.json
else
    array=(${polarion_url//=// })
    polarion_run_name=${array[1]]}
    polarion_url_str="[${polarion_run_name}](${polarion_url})"
    sed -i -e "s,Polarion_URL,${polarion_url_str},g" rhv_rp_conf.json
fi

# 5. set BUILD_URL
array=(${BUILD_URL//\// })
if [[ "${array[@]}" == "" ]]; then
    sed -i -e "s/BUILD_URL/None/g" rhv_rp_conf.json
else
    build_number=${array[$[${#array[@]} - 1]]}
    build_url_str="[${build_number}](${BUILD_URL})"
    echo "$build_url_str"
    sed -i -e "s#BUILD_URL#${build_url_str}#g" rhv_rp_conf.json
fi

# 6. Set tags (RHV 4.2, RHV 4.3, RHVH 4.2.z, RHVH 4.3.z, RHV 4.4, RHVH 4.4.z)
## Analyze Distro(RHEL-7.7, RHVH-4.2-20200522.0) to get variant(RHVH or RHEL)
array=(${Distro//-/ })
variant=${array[0]}
if [[ ${variant} == "RHVH" ]]; then
    # Get rhv version from Distro
    rhv_version=${array[1]}
    # Set 2 tags for RHVH testing
    TAG1="RHVH-${rhv_version}.z"
    TAG2="RHV-${rhv_version}"
    sed -i -e "s/\"TAG\"/\"${TAG1}\", \"${TAG2}\"/g" rhv_rp_conf.json
else
    # Read file RHV_VERSION.PROP to get rhv version
    rhv_version=`cat ${WORKSPACE}/RHV_VERSION.PROP`
    TAG="RHV-${rhv_version}"
    sed -i -e "s/TAG/${TAG}/g" rhv_rp_conf.json
fi

# prepare log dir and log data
mkdir -p reportportal/{results,attachments}
cp nosetests_original.xml reportportal/results

# import test result into ReportPortal
source /home/jenkins/.venv_rp/bin/activate
rp_preproc -c rhv_rp_conf.json -d reportportal/
deactivate
