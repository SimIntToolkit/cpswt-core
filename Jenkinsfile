pipeline {
    agent any
    //agent { 
    //	node {
    //	    label 'docker-agent-latest'
    //	}
    //}
    triggers {
        pollSCM('*/1 * * * *') // poll the SCM every 5 minutes
    }

    stages {
        stage('Clone the repo') {
            steps {
                echo 'Cloning Forked CPSWT-Core..'
                sh 'rm -rf cpswt-core'
                sh 'git clone https://github.com/justinyeh1995/cpswt-core.git'
            }
        }
        stage('Build') {
	    //agent {
	    //	node {
	    //	    label 'docker-agent-latest'// should be able to support the environment for the docker container in this stage
	    //	}
	    //}
            steps {
                echo 'Building projects..'
                sh 'echo "Start a Docker Container for this experiment, which should start the experiment and a archiva server, a inet server, and a omnet++ server.."'
            }
        }
        stage('Test') {
            steps {
                echo 'Testing....'
                sh 'echo "doing test stuff.."'
            }
        }
        stage('Report') {
            steps {
                echo 'Report the results through emails....'
                sh 'echo "doing test stuff.."'
            }
        }
    }

    post {
        always {
    	echo 'This will always run'
	emailext body: "${currentBuild.currentResult}: Job ${env.JOB_NAME} build ${env.BUILD_NUMBER}\n More info at: ${env.BUILD_URL}",
        recipientProviders: [[$class: 'DevelopersRecipientProvider'], [$class: 'RequesterRecipientProvider']],
        subject: "Jenkins Build ${currentBuild.currentResult}: Job ${env.JOB_NAME}",
	attachLog: true
        }
    }	
}
