#!/bin/bash

if [ -z "$RTI_HOME" ]; then
    export RTI_HOME=/usr/local/portico/portico-2.1.0
fi

PROJECT_DIR=${PWD}
JAVA_FOUNDATION_DIR=${PROJECT_DIR}/cpswt-core


mvn_install_deploy() {
	echo "Maven install..."
	mvn clean install -U
	echo "Deploying to Archiva..."
	mvn deploy
}

echo "Maven install and deploy projects from ${JAVA_FOUNDATION_DIR}"
cd ${JAVA_FOUNDATION_DIR}
mvn_install_deploy

echo "=================================================================================="
echo "Completed the compilation, installation, deployment of the C2W foundation packages"
echo "=================================================================================="
