# this script is used to build cpswt-core and its dependencies in the docker container 
/opt/apache-archiva-2.2.5/bin/archiva start
cd /home/cpswt
git clone https://github.com/justinyeh1995/cpswt-core.git
cd cpswt-core/cpswt-core

gradle wrapper --gradle-version=7.3

./gradlew :utils:publish
./gradlew :root:publish
./gradlew :base-events:publish
./gradlew :coa:publish
./gradlew :config:publish
./gradlew :federate-base:publish
./gradlew :federation-manager:publish
./gradlew :fedmanager-host:publish

./gradlew :utils:build 
./gradlew :root:build 
./gradlew :base-events:build 
./gradlew :coa:build 
./gradlew :config:build 
./gradlew :federate-base:build 
./gradlew :federation-manager:build 
./gradlew :fedmanager-host:build