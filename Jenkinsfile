// Do NOT place within the pipeline block 
properties([ [ $class: 'ThrottleJobProperty',
               categories: ['ci_cpswt_build'], 
               limitOneJobWithMatchingParams: false,
               maxConcurrentPerNode: 1,
               maxConcurrentTotal: 1,
               paramsToUseForLimit: '',
               throttleEnabled: true,
               throttleOption: 'category' ] ])

pipeline {
    agent any
    triggers {
        pollSCM('*/1 * * * *') // poll the SCM every 1 minutes
    }

    stages {
        stage('Clone repository') {
            steps {
                echo 'Cloning Forked CPSWT-Core..'
                deleteDir() // Delete workspace before cloning
                sh 'git clone git@github.com:justinyeh1995/CI_with_Jenkins.git'
            }
        }
        stage('Build image') {
            steps {
                echo 'Start a Docker Container for this experiment, which should start the experiment and a archiva server, a inet server, and a omnet++ server..'
                dir("CI_with_Jenkins/cpswt-core") {
                    sh 'docker build -t cpswt-core:latest -f Dockerfile .'
                }
            }
        }
        stage('Deploy image') {
            steps {
                echo 'Run the Docker Container inside Jenkins container'
                sh 'docker run \
                        --name cpswt-core \
                        -p 8080:8080 \
                        cpswt-core:latest'
            }
        }
        stage('Wait for container to stop') {
            options {
                timeout(time: 30, unit: 'MINUTES')
            }
            steps {
                echo 'Wait for container to stop'
                sh 'docker wait cpswt-core'
            }
        }
        stage('Archive loggings') {
            steps {
                echo 'Archiving the results...'
                sh 'docker logs cpswt-core > cpswt-core.log'
                archiveArtifacts artifacts: 'cpswt-core.log', fingerprint: true
            }
        }
    }

    post {
        always {
    	echo 'This will always run'
	    emailext body: "${currentBuild.currentResult}: Job ${env.JOB_NAME} build ${env.BUILD_NUMBER}\n More info at: ${env.BUILD_URL}",
                recipientProviders: [[$class: 'DevelopersRecipientProvider'], [$class: 'RequesterRecipientProvider']],
                subject: "Jenkins Build ${currentBuild.currentResult}: Job ${env.JOB_NAME}",
                attachLog: true,
                attachmentsPattern: 'cpswt-core.log'
        echo 'Tearing Down the image & container....'
        sh 'docker rm -f cpswt-core'
        sh 'docker rmi cpswt-core:latest'
        sh 'rm -rf cpswt-core'
        }
    }	
}
