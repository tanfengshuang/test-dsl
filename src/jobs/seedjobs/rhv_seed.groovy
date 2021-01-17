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
        definition {
            cps {
                script(readFileFromWorkspace(config['Jenkinsfile']))
                sandbox()
            }
        }
    }
}