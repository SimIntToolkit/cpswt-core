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
                git url: 'https://github.com/justinyeh1995/cpswt-core.git', branch: 'develop'
            }
        }
        stage('Build image') {
            steps {
                echo 'Start a Docker Container for this experiment, which should start the experiment and a archiva server, a inet server, and a omnet++ server..'
                sh 'cd cpswt-core'
                sh 'docker build -t cpswt-core:latest -f Dockerfile .'
            }
        }
        stage('Check network') {
            steps {
                echo 'Checking if cpswt-core network is in docker network....'
                sh 'set +e'
                sh 'docker network ls | grep cpswt-core'
                sh 'if [ $? -eq 0 ]; then echo "cpswt-core network exists"; else docker network create cpswt-core; fi'
            }
        }
        stage('Deploy image') {
            steps {
                echo 'Run the Docker Container inside Jenkins container'
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
        stage('Clean up') {
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

// pipeline {
//     agent any
//     triggers {
//         pollSCM('*/1 * * * *') // poll the SCM every 1 minute
//     }

//     stages {
//         stage('Clone repository') {
//             steps {
//                 echo 'Cloning Forked CPSWT-Core..'
//                 deleteDir() // Delete workspace before cloning
//                 git url: 'https://github.com/justinyeh1995/cpswt-core.git', branch: 'develop'
//             }
//         }
//         stage('Build image') {
//             steps {
//                 echo 'Start a Docker Container for this experiment, which should start the experiment and an Archiva server, an Inet server, and an OMNeT++ server..'
//                 dir('cpswt-core') {
//                     docker.build('cpswt-core:latest')
//                 }
//             }
//         }
//         stage('Check network') {
//             steps {
//                 echo 'Checking if cpswt-core network is in Docker network....'
//                 script {
//                     def networkExists = sh(script: 'docker network ls | grep cpswt-core', returnStatus: true)
//                     if (networkExists == 0) {
//                         echo "cpswt-core network exists"
//                     } else {
//                         sh 'docker network create cpswt-core'
//                     }
//                 }
//             }
//         }
//         stage('Deploy image') {
//             steps {
//                 echo 'Run the Docker Container....'
//                 script {
//                     docker.image('cpswt-core:latest').withRun('--name cpswt-core \
//                             --restart=on-failure \
//                             --detach \
//                             --network cpswt-core \
//                             --env DOCKER_HOST=tcp://docker:2376 \
//                             --env DOCKER_CERT_PATH=/certs/client \
//                             --env DOCKER_TLS_VERIFY=1 \
//                             --publish 8081:8080 \
//                             -v /var/run/docker.sock:/var/run/docker.sock') {
//                         // Additional setup or commands inside the container if needed
//                     }
//                 }
//             }
//         }
//         stage('Archive loggings') {
//             steps {
//                 echo 'Archiving the results...'
//                 script {
//                     docker.image('cpswt-core:latest').inside('-v $PWD:/app') {
//                         sh 'docker logs cpswt-core > cpswt-core.log'
//                     }
//                 }
//                 archiveArtifacts artifacts: 'cpswt-core.log', fingerprint: true
//             }
//         }
//         stage('Clean up') {
//             steps {
//                 echo 'Tearing Down the image & container....'
//                 sh 'docker rm -f cpswt-core'
//                 sh 'docker rmi cpswt-core:latest'
//             }
//         }
//     }

//     post {
//         always {
//             echo 'This will always run'
//             emailext body: "${currentBuild.currentResult}: Job ${env.JOB_NAME} build ${env.BUILD_NUMBER}\n More info at: ${env.BUILD_URL}",
//             recipientProviders: [[$class: 'DevelopersRecipientProvider'], [$class: 'RequesterRecipientProvider']],
//             subject: "Jenkins Build ${currentBuild.currentResult}: Job ${env.JOB_NAME}",
//             attachLog: true,
//             attachmentsPattern: 'cpswt-core.log'
//         }
//     }   
// }
