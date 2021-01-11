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
        environment {
            PROD_ACC = credentials('ent_prod_acc')
            STAGE_ACC = credentials('rhv_stage_acc')
        }
        throttleConcurrentBuilds {
            maxPerNode(3)
            maxTotal(0)
        }
        options {
            timestamps()
            ansiColor('xterm')
            buildDiscarder(logRotator(numToKeepStr: '100', artifactNumToKeepStr: '-1'))
        }

        definition {
            cps {
                script(readFileFromWorkspace(config['Jenkinsfile']))
                sandbox()
            }
        }
    }
}