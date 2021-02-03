import os
import commands

errata_id = os.environ["ERRATA_ID"]
test_product = os.environ['Test_Product']
product_type = os.environ['Product_Type']
variant = os.environ['Variant']
arch = os.environ['Arch']
test_level = os.environ["Test_Level"]
cdn = os.environ['CDN']
build_url = os.environ['BUILD_URL']

def get_launch_name():
    """
    Get ReportPortal launch name.

    RHEL7: Errata-49798_RHEL7_Server_x86_64_Full_ProdCDN
    RHEL8: Errata-53717_RHEL8_x86_64_Full_ProdCDN

    :return:
    """

    if product_type == "RHEL7":
        launch_name = "Errata-{0}_{1}_{2}_{3}_{4}_{5}CDN".format(errata_id, product_type, variant, arch, test_level, cdn)
        
    elif product_type == "RHEL8":
        launch_name = "Errata-{0}_{1}_{2}_{3}_{4}CDN".format(errata_id, product_type, arch, test_level, cdn)

    return launch_name

def set_variable_value():
    """
    Update ReportPortal config file to replace variable with value

    1. Replace variable: PROJECT_NAME
    2. Replace variable: LAUNCH_NAME
    3. Replace variable: ERRATA_URL, BUILD_URL
    4. Replace variable: LAUNCH_TAG
    """

    mp_rp_conf_file = 'entitlement-tests/CCI/ReportPortal/mp_rp_conf.json'
    
    # 1. Set project name which is just the test product name with upper case letter
    cmd = "sed -i -e 's/PROJECT_NAME/{0}/g' {1}".format(test_product.upper(), mp_rp_conf_file)
    (ret, output) = commands.getstatusoutput(cmd)
    
    # 2. Set launch name
    # Launch name examples - Errata-49798_RHEL7_Server_x86_64_Full_ProdCDN; Errata-53717_RHEL8_x86_64_Full_ProdCDN
    cmd = "sed -i -e 's/LAUNCH_NAME/{0}/g' {1}".format(get_launch_name(), mp_rp_conf_file)
    (ret, output) = commands.getstatusoutput(cmd)
    
    # 3. Set variables value in description of launch
    # a) Set Errata url in description of launch
    errata_url = "[{0}](https:\/\/errata.devel.redhat.com\/advisory\/{1})".format(errata_id, errata_id)
    cmd = "sed -i -e 's/ERRATA_URL/{0}/g' {1}".format(errata_url, mp_rp_conf_file)
    (ret, output) = commands.getstatusoutput(cmd)
    
    # b) Set jenkins job url in description of launch
    build_id = build_url.strip('/').split('/')[-1]
    build_url_str = "[{0}]({1})".format(build_id, build_url.replace("/","\/"))
    
    cmd = "sed -i -e 's/BUILD_URL/{0}/g' {1}".format(build_url_str, mp_rp_conf_file)
    (ret, output) = commands.getstatusoutput(cmd)
    
    # 4. Set launch tag
    # Tag examples - OpenStack16; Ceph3; CNV2
    cmd = "cat product_version.txt"
    (ret, output) = commands.getstatusoutput(cmd)
    
    cmd = "sed -i -e 's/LAUNCH_TAG/{0}{1}/g' {2}".format(test_product, output, mp_rp_conf_file)
    (ret, output) = commands.getstatusoutput(cmd)

if __name__ == '__main__':
    set_variable_value()
