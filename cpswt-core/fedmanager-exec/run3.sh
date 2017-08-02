#!/usr/bin/env bash

RTI_RID_FILE=RTI.rid
java \
-Djava.net.preferIPv4Stack=true \
-Dorg.apache.logging.log4j.simplelog.StatusLogger.level=TRACE \
-Dlog4j.configurationFile=conf/log4j2.xml \
-jar fedmanager-exec-0.5.0-SNAPSHOT.jar \
-configFile conf/triple/fedmgrconfig.json
