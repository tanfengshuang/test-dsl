#!/usr/bin/env groovy

import com.redhat.jenkins.plugins.ci.messaging.MessagingProviderOverrides;
MessagingProviderOverrides po = new MessagingProviderOverrides('Consumer.rh-jenkins-ci-plugin.cb8a05ac-493c-42ee-9529-0aa113fc35ba.VirtualTopic.qe.ci.>');
pipelines {
    stages {
        agent {
            label('linchpin')
        }
        environment {
            PROD_ACC = credentials('ent_prod_acc')
            STAGE_ACC = credentials('rhv_stage_acc')
        }
        options {
            timestamps()
            ansiColor('xterm')
            buildDiscarder(logRotator(numToKeepStr: '100', artifactNumToKeepStr: '-1'))
        }
        stage('Preparation') {
            steps {
                script {
                    currentBuild.displayName = "#${env.BUILD_ID}-${ERRATA_ID}-${CDN}"
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
                sh 'bash -x $WORKSPACE/entitlement-tests/CCI/scripts/RHV/rhv_defaults_preparation.sh'
            }
        }
        stage('Provision') {
            when { expression { env.Testing_System == '' } }
            steps {
                sh 'bash -x $WORKSPACE/entitlement-tests/CCI/scripts/RHV/rhv_defaults_provision.sh'
            }
        }
        stage('Testing') {
            steps {
                sh 'bash -x $WORKSPACE/entitlement-tests/CCI/scripts/RHV/rhv_defaults_testing.sh'
            }
        }
    }
    post {
        always {
            script {
                if (fileExists('nosetests_original.xml')) {
                    try {
                        sh 'bash -x $WORKSPACE/entitlement-tests/CCI/scripts/RHV/rhv_defaults_polarion.sh'
                        script {
                            importPolarionXUnit files: 'nosetests.xml',
                                    wait: true,
                                    mark: false,
                                    excludes: '',
                                    defaultExcludes: true,
                                    caseSensitive: true,
                                    server: 'https://polarion.engineering.redhat.com/polarion/import/xunit',
                                    user: 'platformqe_machine',
                                    password: hudson.util.Secret.fromString('polarion'),
                                    providerName: 'Red Hat UMB',
                                    overrides: po,
                                    timeout: 60
                        }
                    } catch (Exception e) {
                            sh 'echo "POLARION FAILED, SKIPPING TO REPORT PORTAL"'
                        }
                    sh 'bash -x $WORKSPACE/entitlement-tests/CCI/scripts/RHV/rhv_defaults_reportportal.sh'
                } else {
                        echo 'NOSETESTS_ORIGINAL.XML DOES NOT EXIST, CANCELLING IMPORTS'
                    }
            }
            script {
                archiveArtifacts artifacts: '*.txt, *.conf, *.json, *.xml, **/redhat.repo, **/**/**/*.log, **/manifest/*.xml, **/manifest/*.json'
            }
        }
    }
}