properties([
    parameters([
        string(name: 'RHCERT_VERSION', defaultValue: '7.18', description: 'This is rhcert release version'),
        string(name: 'TOKEN', defaultValue: '6a30f695-23c8-4180-9baa-6c20838ed94b', description: 'token to authenticate to report portal'),
        string(name: 'IP', defaultValue: 'https://reportportal-rhcertqe.cloud.paas.psi.redhat.com', description: 'report portal IP'),
        string(name: 'project_name', defaultValue: 'rhcert_api', description: 'this option is used to set project name'),
        string(name: 'buildid', defaultValue: '', description: 'This is rhcert build id'),
        string(name: 'env', defaultValue: 'qa', description: 'hydra env'),
        string(defaultValue:'', description: 'ci', name: 'CI_MESSAGE'),
        string(defaultValue:'', description: 'host IP on which you want to run test', name: 'Host_IP')
    ])
])

params.each { k, v -> env[k] = v }

pipeline{
    agent any
    options{
        disableConcurrentBuilds()
        buildDiscarder( logRotator( daysToKeepStr: '30', numToKeepStr: '30', artifactDaysToKeepStr: '15', artifactNumToKeepStr: '15' ) )
    }
    stages{
        stage('Test 2'){
            steps{
                 sh 'echo  testing!!!!!!'
            }
        }
        stage('Test 2'){
            steps{
                 sh 'echo  testing!!!!!!'
            }
        }
    }
}
