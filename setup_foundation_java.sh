#!/bin/bash
# Author: Yogesh Barve
#export RTI_HOME=/home/vagrant/c2wt-dev/dependency/portico/portico-2.1.0
export RTI_HOME=/usr/local/portico/portico-2.1.0

mvn_install_deploy() {
	echo "Maven Compiling...."
	mvn clean install -U
	echo "Maven Deploying to the Archiva....."
	mvn deploy
	echo "Deployment Completed...."

}

traverse_dir(){
	echo ${1}
	cd ${1} 
	mvn_install_deploy
	cd ..
}

PROJECT_DIR=${PWD}
JAVA_FOUNDATION_DIR=$PROJECT_DIR/c2w-foundation 
C2WJNI=$PROJECT_DIR/c2w-jni


echo "=================================================================================="
echo "Compiling the C2W-JNI in :" $C2WJNI
cd $C2WJNI
mvn_install_deploy
traverse_dir "processid"
mvn_install_deploy
cd ..


echo "=================================================================================="

echo "Entering foundation Directory: " $JAVA_FOUNDATION_DIR
cd $JAVA_FOUNDATION_DIR/
mvn_install_deploy

echo "=================================================================================="
echo "Completed the compilation, installation, deployment of the C2W foundation packages"
echo "=================================================================================="
