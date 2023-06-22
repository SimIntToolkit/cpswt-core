# this script is used to build cpswt-core and its dependencies in the docker container 
ORIGINAL_PATH=$PATH
export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-arm64
export PATH=$JAVA_HOME/bin:$PATH
/opt/apache-archiva-2.2.5/bin/archiva start

# wait for archiva to start
# nc -zv localhost 8080
sleep 20
# create admin user
curl --no-progress-meter -X POST -H "Content-Type: application/json" -H "Origin: http://localhost:8080" -d @- \
 http://localhost:8080/restServices/redbackServices/userService/createAdminUser <<'TERMINUS'
{
    "username": "admin",
    "password": "adminpass123",
    "email": "admin@archiva-test.org",
    "fullName": "Admin",
    "locked": false,
    "passwordChangeRequired": false,
    "permanent": false,
    "readOnly": false,
    "validated": true,
    "confirmPassword": "adminpass123"
}
TERMINUS

# switch to java 17
unset JAVA_HOME
export JAVA_HOME=/usr/lib/jvm/java-1.17.0-openjdk-arm64
export PATH=$JAVA_HOME/bin:$ORIGINAL_PATH

# clone cpswt-core and build it
cd /home/cpswt
git clone https://github.com/justinyeh1995/cpswt-core.git
cd cpswt-core/cpswt-core

gradle wrapper --gradle-version=7.5

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

cd /home/cpswt/cpswt-core/examples/HelloWorldJava
gradle wrapper --gradle-version=7.5
./gradlew :runFederationBatch