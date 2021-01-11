import com.redhat.jenkins.plugins.ci.messaging.MessagingProviderOverrides;

MessagingProviderOverrides po = new MessagingProviderOverrides('Consumer.rh-jenkins-ci-plugin.cb8a05ac-493c-42ee-9529-0aa113fc35ba.VirtualTopic.qe.ci.>');

pipeline {
    agent { label "linchpin" }
    options {
        timestamps()
        ansiColor('xterm')
    }
stages {
    stage("Builder") {
        steps {
            cleanWs()
            git url: 'http://git.app.eng.bos.redhat.com/git/entitlement-tests.git', branch: 'master'
            sh '''#!/bin/bash
               rm -rf xml
               mkdir xml
               pushd xml
               xml_url_list="$XML_URL_1 $XML_URL_2"
               for url in $xml_url_list; do
                   wget --no-check-certificate $url
               done
               popd
               python CI/polarion/merge_polarion_xml_log.py xml/*
               '''
            archiveArtifacts artifacts: 'nosetests-final.xml'
            importPolarionXUnit files: 'nosetests-final.xml', \
              wait: true, \
              mark: true, \
              excludes: '', \
              defaultExcludes: true, \
              caseSensitive: true, \
              server: 'https://polarion.engineering.redhat.com/polarion/import/xunit', \
              user: 'platformqe_machine', \
              password: hudson.util.Secret.fromString('polarion'), \
              providerName: 'Red Hat UMB', \
              overrides: po, \
              timeout: 60
            }
        }
    }
}

