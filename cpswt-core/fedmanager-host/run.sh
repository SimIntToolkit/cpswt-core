#!/usr/bin/env bash

CPSWT_ROOT=$HOME/cpswt
RTI_RID_FILE=RTI.rid
LOG4J="-Dlog4j.configurationFile=$DIR/conf/log4j2.xml"
IPV4="-Djava.net.preferIPv4Stack=true"

java \
-D$IPV4 \
-Dorg.apache.logging.log4j.simplelog.StatusLogger.level=TRACE \
-D$LOG4J \
-jar fedmanager-exec-0.5.0-SNAPSHOT.jar \
-configFile conf/fedmgrconfig.json

