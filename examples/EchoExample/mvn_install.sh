#!/usr/bin/env bash

mvn_install_deploy() {
	mvn clean install -U
	# mvn deploy
}

ROOT_DIR=`pwd`
FEDERATION=EchoExample

cd ${ROOT_DIR}/${FEDERATION}_1_FederatesExporter/java-federates/${FEDERATION}-base-java/
mvn_install_deploy
cd ${ROOT_DIR}/${FEDERATION}_1_FederatesExporter/java-federates/${FEDERATION}-rti-java
mvn_install_deploy
cd ${ROOT_DIR}/${FEDERATION}_1_FederatesExporter/
mvn_install_deploy

cd ${ROOT_DIR}/${FEDERATION}_2_Implementation/${FEDERATION}-impl-java/
mvn_install_deploy
cd ${ROOT_DIR}/${FEDERATION}_2_Implementation/
mvn_install_deploy

cd ${ROOT_DIR}