pipeline {
    agent any
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
        stage('Build the image') {
            steps {
                echo 'Start a Docker Container for this experiment, which should start the experiment and a archiva server, a inet server, and a omnet++ server..'
                sh 'cd cpswt-core && docker build -t cpswt-core:latest -f Dockerfile .'
            }
        }
        stage('Check docker network') {
            steps {
                echo 'Checking if cpswt-core network is in docker network....'
                sh 'docker network ls | grep cpswt-core'
                sh 'if [ $? -eq 0 ]; then echo "cpswt-core network exists"; else docker network create cpswt-core; fi'
            }
        }
        stage('Run the container') {
            steps {
                echo 'Run the Docker Container....'
                sh 'echo "run the container."'
                sh 'docker run \
                        --name cpswt-core \
                        --restart=on-failure \
                        --detach \
                        --network cpswt-core \
                        --env DOCKER_HOST=tcp://docker:2376 \
                        --env DOCKER_CERT_PATH=/certs/client \
                        --env DOCKER_TLS_VERIFY=1 \
                        --publish 8081:8080 \
                        -v /var/run/docker.sock:/var/run/docker.sock \
                        cpswt-core:latest'
            }
        }
        stage('Archive loggings') {
            steps {
                echo 'Archiving the results...'
                sh 'docker logs cpswt-core > cpswt-core.log'
                archiveArtifacts artifacts: 'cpswt-core.log', fingerprint: true
            }
        }
        stage('Tear down the Docker Container') {
            steps {
                echo 'Tearing Down the image & container....'
                sh 'docker rm -f cpswt-core'
                sh 'docker rmi cpswt-core:latest'
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
        }
    }	
}
