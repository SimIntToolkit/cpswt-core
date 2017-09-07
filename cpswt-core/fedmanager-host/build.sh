mvn clean install -U
DIR=`pwd`
cd target
zip -r fedmanager.zip fedmanager-exec-0.5.0-SNAPSHOT.jar ../run*.sh ../curl* RTI.rid conf fom js
cd $DIR
