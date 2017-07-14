#!/usr/bin/env bash

FEDMANAGER_PROFILE="FederationManagerExecJava"
mvn exec:java -U -X -P ${FEDMANAGER_PROFILE}