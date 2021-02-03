#!/bin/bash

# add test properties into nosetests.xml
if [ -f "nosetests_original.xml" ];then
    echo Prepare to add properies into nosetests.xml file.............
    python entitlement-tests/CCI/polarion/add_polarion_properties_rhv.py
else
    echo "No nosetest_original.xml, exit!"
fi
