#!/usr/bin/env bash
echo "starting Source1 Federate..."
C2WT_MVN_EXEC_PROFILE="JavaFed,Source1"
cd HelloWorld_Java_Deployment
mvn package exec:exec -P $C2WT_MVN_EXEC_PROFILE