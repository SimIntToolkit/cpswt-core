pipeline {
    agent { 
    	node {
    	    label 'docker-agent-latest'
    	}
    }
    triggers {
	pollSCM('H/5 * * * *') // poll the SCM every 5 minutes
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
}
