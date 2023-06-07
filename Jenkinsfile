pipeline {
    agent { 
    	node {
    	    label 'docker-agent-latest'
    	}
    }
    triggers {
        pollSCM('H/3 * * * *') // poll the SCM every 5 minutes
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
            steps {
                echo 'Building projects..'
                sh 'echo "doing test stuff.."'
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
	to: "chihtingyeh1995@gmail.com",
        subject: "Jenkins Build ${currentBuild.currentResult}: Job ${env.JOB_NAME}",
	attachLog: true
    	//emailext body: "${DEFAULT_CONTENT}",
        //recipientProviders: [[$class: 'DevelopersRecipientProvider'], [$class: 'RequesterRecipientProvider']],
        //subject: "${DEFAULT_SUBJECT}"
        }
    }	
}
