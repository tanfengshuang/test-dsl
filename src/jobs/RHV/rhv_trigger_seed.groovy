def gitURL = "https://github.com/tanfengshuang/test-dsl/"
def JenkinsfilePath = "src/jobs/RHV/"
def jobConfig = [
        'try_rhv_stage_trigger'        : ['jobDesc': 'Upstream UMB triggerred job for Stage CDN.',
                                          'jobDisp': 'RHV UMB trigger of Stage'
        ],
        'try_rhv_prod_trigger'	       : ['jobDesc': 'Upstream UMB triggerred job for Prod CDN.',
                                          'jobDisp': 'RHV UMB trigger of Prod'
        ],
        'try_rhv_manual_trigger'	       : ['jobDesc': 'Upstream manual triggered job.',
                                              'jobDisp': 'RHV Manual trigger'
        ]
]

jobConfig.each { jobName, config ->
    pipelineJob(jobName) {
        disabled()
        description(config['jobDesc'])
        displayName(config['jobDisp'])

        properties {
            cachetJobProperty {
                requiredResources(true)
                resources(["beaker-master", "umb", "manifest-api"])
            }
        }
        throttleConcurrentBuilds {
            maxPerNode(3)
            maxTotal(0)
        }

        if (jobName.contains("rhv_stage_trigger")){
            triggers {
                ciBuildTrigger {
                    noSquash(true)
                    providers {
                        providerDataEnvelope{
                            providerData {
                                activeMQSubscriber {
                                    name("Red Hat UMB")
                                    overrides {
                                        topic("Consumer.rh-jenkins-ci-plugin.${{UUID.randomUUID().toString()}}.VirtualTopic.eng.errata.activity.status")
                                    }
                                    selector("from = 'QE' AND to = 'REL_PREP'and product = 'RHV'")
                                    checks {
                                        msgCheck {
                                            field('$.content_types[0]')
                                            expectedValue('rpm')
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (jobName.contains("rhv_prod_trigger")){
            triggers {
                ciBuildTrigger {
                    noSquash(true)
                    providers {
                        providerDataEnvelope{
                            providerData {
                                activeMQSubscriber {
                                    name("Red Hat UMB")
                                    overrides {
                                        topic("Consumer.rh-jenkins-ci-plugin.${{UUID.randomUUID().toString()}}.VirtualTopic.eng.errata.activity.status")
                                    }
                                    selector("from = 'IN_PUSH' AND to = 'SHIPPED_LIVE'and product = 'RHV'")
                                    checks {
                                        msgCheck {
                                            field('$.content_types[0]')
                                            expectedValue('rpm')
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        parameters {
            if(jobName.contains("rhv_manual_trigger")){
                stringParam('ERRATA_ID', '', 'Set ERRATA_ID or Manifest_URL for manual trigger. Manifest_URL will take affect if both are set.')
                stringParam('Manifest_URL', '', 'Set ERRATA_ID or Manifest_URL for manual trigger. Manifest_URL will take affect if both are set.')
                choiceParam('CDN', ['Stage', 'Prod'], 'Select CDN, Stage or Prod.')
                choiceParam('Candlepin', ['Stage', 'Prod'], 'Select Candlepin, Stage or Prod.')
        }

        definition {
            cps {
                script(readFileFromWorkspace('src/jobs/RHV/rhv_trigger.Jenkinsfile'))
                sandbox()
            }
        }
    }
}