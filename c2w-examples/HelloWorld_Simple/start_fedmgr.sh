#!/usr/bin/env bash
echo "starting Source1 Federate..."
C2WT_MVN_EXEC_PROFILE="FedManager"
cd HelloWorld_Java_Deployment
mvn package exec:exec -P $C2WT_MVN_EXEC_PROFILE