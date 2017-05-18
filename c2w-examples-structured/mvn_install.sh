#!/usr/bin/env bash

mvn_install_deploy() {
	mvn clean install -U
	mvn deploy
}

ROOT_DIR=`pwd`

cd ${ROOT_DIR}/HelloWorld_Java_Tutorial_1_FederatesExporter/java-federates/HelloWorld_Java_Tutorial-base-java/
mvn_install_deploy
cd ${ROOT_DIR}/HelloWorld_Java_Tutorial_1_FederatesExporter/java-federates/HelloWorld_Java_Tutorial-rti-java
mvn_install_deploy
cd ${ROOT_DIR}/HelloWorld_Java_Tutorial_1_FederatesExporter/
mvn_install_deploy

cd ${ROOT_DIR}/HelloWorld_Java_Tutorial_2_Implementation/HelloWorld_Java_Tutorial-impl-java/
mvn_install_deploy
cd ${ROOT_DIR}/HelloWorld_Java_Tutorial_2_Implementation/
mvn_install_deploy

cd ${ROOT_DIR}