def gitURL = "https://github.com/tanfengshuang/test-dsl/"
def JenkinsfilePath = "src/jobs/RHV/"
def jobConfig = [
        'try_rhv_server_x86_64'        : ['jobDesc': 'This job validates RHV Entitlements on RHEL Server x86_64.',
                                      'jobDisp': 'RHV on RHEL Server x86_64',
                                      'Jenkinsfile': JenkinsfilePath + 'rhv_server_x86_64.Jenkinsfile'
        ],
        'try_rhv_rhvh_x86_64'	       : ['jobDesc': 'This job validates RHV Entitlements on RHVH x86_64.',
                                      'jobDisp': 'RHV on RHVH x86_64',
                                      'Jenkinsfile': JenkinsfilePath + 'rhvh_x86_64.Jenkinsfile'
        ]
]

["git","clone",gitURL].execute()

jobConfig.each { jobName, config ->
    pipelineJob(jobName) {
        disabled()
        description(config['jobDesc'])
        displayName(config['jobDisp'])

        properties {
            cachetJobProperty {
                requiredResources(true)
                resources(["beaker", "umb", "manifest-api"])
            }
        }
        throttleConcurrentBuilds {
            maxPerNode(3)
            maxTotal(0)
        }
        if(jobName.contains("rhv_server_x86_64")){
            parameters {
                string(name: 'ERRATA_ID', defaultValue: '', description: 'Set ERRATA_ID or Manifest_URL for manual trigger. Manifest_URL will take affect if both are set.')
                string(name: 'Manifest_URL', defaultValue: '', description: 'Set ERRATA_ID or Manifest_URL for manual trigger. Manifest_URL will take affect if both are set.')
                string(name: 'Distro', defaultValue: 'RHEL-7.9-updates-20201102.0', description: 'Fill in RHEL-H Distro here, such as RHEL-7.7.')
                string(name: 'PID', defaultValue: '', description: 'Set RHV product ids listed in package manifest, RHV PIDs on Server_x86_64 - 69, 150, 415, 421, 479.')
                choice(name: 'Variant', choices: 'Server\nBaseOS', description: 'Select Variant.')
                choice(name: 'Arch', choices: 'x86_64\n', description: 'Select Arch.')
                choice(name: 'CDN', choices: 'Stage\nProd', description: 'Select CDN, Stage or Prod.')
                choice(name: 'Candlepin', choices: 'Stage\nProd', description: 'Select Candlepin, Stage or Prod.')
                string(name: 'Testing_System', defaultValue: '', description: 'Optional, fill in the hostname or IP of one system which has correct RHV version and arch here, then will do testing on it rather than provison beaker system.')
                string(name: 'Password', defaultValue: 'QwAo2U6GRxyNPKiZaOCx', description: "Optional, used for Testing_System, the default value above is beaker's default password, please modify it if needed, but please make sure correct password here.")
            }
        }
        if(jobName.contains("rhv_rhvh_x86_64")){
            parameters {
                string(name: 'ERRATA_ID', defaultValue: '', description: 'Set ERRATA_ID or Manifest_URL for manual trigger. Manifest_URL will take affect if both are set.')
                string(name: 'Manifest_URL', defaultValue: '', description: 'Set ERRATA_ID or Manifest_URL for manual trigger. Manifest_URL will take affect if both are set.')
                string(name: 'Distro', defaultValue: 'RHEL-7.9-updates-20201102.0', description: 'Fill in RHEL-H Distro here, such as RHEL-7.7.')
                string(name: 'PID', defaultValue: '', description: 'Set RHV product ids listed in package manifest, RHV PIDs on Server_x86_64 - 69, 150, 415, 421, 479.')
                choice(name: 'Variant', choices: 'Server\nBaseOS', description: 'Select Variant.')
                choice(name: 'Arch', choices: 'x86_64\n', description: 'Select Arch.')
                choice(name: 'CDN', choices: 'Stage\nProd', description: 'Select CDN, Stage or Prod.')
                choice(name: 'Candlepin', choices: 'Stage\nProd', description: 'Select Candlepin, Stage or Prod.')
                string(name: 'Testing_System', defaultValue: '', description: 'Optional, fill in the hostname or IP of one system which has correct RHV version and arch here, then will do testing on it rather than provison beaker system.')
                string(name: 'Password', defaultValue: 'QwAo2U6GRxyNPKiZaOCx', description: "Optional, used for Testing_System, the default value above is beaker's default password, please modify it if needed, but please make sure correct password here.")
            }
        }
        definition {
            cps {
                script(readFileFromWorkspace('rhv.Jenkinsfile'))
                sandbox()
            }
        }
    }
}