#!/usr/bin/env groovy

import com.redhat.jenkins.plugins.ci.messaging.MessagingProviderOverrides;
MessagingProviderOverrides po = new MessagingProviderOverrides('Consumer.rh-jenkins-ci-plugin.cb8a05ac-493c-42ee-9529-0aa113fc35ba.VirtualTopic.qe.ci.>');

pipeline {
    agent {
        label('linchpin')
    }
    options {
        timestamps()
        ansiColor('xterm')
        buildDiscarder(logRotator(numToKeepStr: '100', artifactNumToKeepStr: '-1'))
    }
    stages {
        stage('Preparation') {
            steps {
                script {
                    currentBuild.displayName = "${CI_MESSAGE.errata_id}-${CI_MESSAGE.synopsis}"
                }
                cleanWs()
                dir( 'entitlement-tests' )
                {
                    checkout scm: [
                        $class: 'GitSCM',
                        extensions: [[$class: 'CleanCheckout']],
                        userRemoteConfigs: [[credentialsId: 'gerrit-ftan',url: 'ssh://ftan@code.engineering.redhat.com/entitlement-tests']],
                        branches: [[name: 'master']]
                    ]
                }
                sh 'bash -x $WORKSPACE/entitlement-tests/CCI/scripts/RHV/rhv_umb_trigger_stage_preparation.sh'
            }
        }
        stage('Build Downstream Jobs') {
            parallel {
                stage('RHVH_x86_64') {
                    when {
                        expression { return readFile('properties.check').contains('rhvh_RHVH_x86_64.dir') }
                    }
                    steps {
                        script {
                            build job: "try_rhv_rhvh_x86_64",
                            wait: false,
                            parameters: [
                                string(name: 'Distro', value: readFile('rhvh_RHVH_x86_64.dir/Distro')),
                                string(name: 'MANIFEST_URL', value: readFile('rhvh_RHVH_x86_64.dir/MANIFEST_URL')),
                                string(name: 'ERRATA_ID', value: readFile('rhvh_RHVH_x86_64.dir/ERRATA_ID')),
                                string(name: 'Candlepin', value: readFile('rhvh_RHVH_x86_64.dir/Candlepin')),
                                string(name: 'CDN', value: readFile('rhvh_RHVH_x86_64.dir/CDN')),
                                string(name: 'Variant', value: readFile('rhvh_RHVH_x86_64.dir/Variant')),
                                string(name: 'Arch', value: readFile('rhvh_RHVH_x86_64.dir/Arch')),
                                string(name: 'PID', value: readFile('rhvh_RHVH_x86_64.dir/PID'))
                            ]
                        }
                    }
                }
                stage('Server_x86_64') {
                    when {
                        expression { return readFile('properties.check').contains('rhv_Server_x86_64.dir') }
                    }
                    steps {
                        script {
                            build job: "try_rhv_server_x86_64",
                            wait: false,
                            parameters: [
                                string(name: 'Distro', value: readFile('rhv_Server_x86_64.dir/Distro')),
                                string(name: 'MANIFEST_URL', value: readFile('rhv_Server_x86_64.dir/MANIFEST_URL')),
                                string(name: 'ERRATA_ID', value: readFile('rhv_Server_x86_64.dir/ERRATA_ID')),
                                string(name: 'Candlepin', value: readFile('rhv_Server_x86_64.dir/Candlepin')),
                                string(name: 'CDN', value: readFile('rhv_Server_x86_64.dir/CDN')),
                                string(name: 'Variant', value: readFile('rhv_Server_x86_64.dir/Variant')),
                                string(name: 'Arch', value: readFile('rhv_Server_x86_64.dir/Arch')),
                                string(name: 'PID', value: readFile('rhv_Server_x86_64.dir/PID'))
                            ]
                        }
                    }
                }
            }
        }
        stage('Email') {
            steps {
                script {
                    def mailRecipients = 'ftan@redhat.com'
                    def jobName = currentBuild.fullDisplayName
                    emailext body: '''${SCRIPT, template="groovy-html.template"}''',
                    mimeType: 'text/html',
                    subject: "[NNNNNN Test Pipeline][Jenkins] ${jobName}",
                    from: "jenkins@redhat.com",
                    to: "${mailRecipients}",
                    replyTo: "${mailRecipients}",
                    recipientProviders: [[$class: 'CulpritsRecipientProvider']]
                }
            }
        }
    }
    post {
        always {
            script {
                archiveArtifacts artifacts: '*.properties, *.dir/*, **/manifests/*.json'
            }
        }
    }
}