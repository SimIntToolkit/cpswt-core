# this script is used to build cpswt-core and its dependencies in the docker container 
ORIGINAL_PATH=$PATH
export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

/opt/apache-archiva-2.2.5/bin/archiva start

# wait for archiva to start
# wait for archiva to start
echo "Waiting archiva to launch on 8080..."

while ! nc -z localhost 8080; do   
  sleep 0.1 # wait for 1/10 of the second before check again
done

echo "archiva launched"

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

# disable rest.csrffilter.enabled in archiva.xml
sed -i '/<rest>/,/<\/csrffilter>/s/<enabled>true/<enabled>false/' /opt/apache-archiva-2.2.5/conf/archiva.xml
# sed -i 's/<baseUrl\/>/<baseUrl>http:\/\/129.59.107.97\/archiva-core\/<\/baseUrl>/' /opt/apache-archiva-2.2.5/conf/archiva.xml

# # restart archiva
/opt/apache-archiva-2.2.5/bin/archiva stop
/opt/apache-archiva-2.2.5/bin/archiva start

echo "Waiting archiva to launch again on 8080..."

while ! nc -z localhost 8080; do   
  sleep 0.1 # wait for 1/10 of the second before check again
done

echo "archiva launched  again"

# switch to java 17
unset JAVA_HOME
export JAVA_HOME=/usr/lib/jvm/java-1.17.0-openjdk-amd64
export PATH=$JAVA_HOME/bin:$ORIGINAL_PATH

# clone cpswt-core and build it
cd /home/cpswt
git clone https://github.com/SimIntToolkit/cpswt-core.git
cd cpswt-core/cpswt-core

gradle wrapper --gradle-version=8.0

ARCHIVA_REPO_URL="http://localhost:8080/repository/snapshots/0.8.0-SNAPSHOT/"

# sh ./cpswt-redeploy.sh
./gradlew :utils:build --rerun-tasks --refresh-dependencies
./gradlew :utils:publish 
echo "utils published"

./gradlew :root:build --rerun-tasks --refresh-dependencies
./gradlew :root:publish
echo "root published"

./gradlew :base-events:build --rerun-tasks --refresh-dependencies
./gradlew :base-events:publish
echo "base-events published"

./gradlew :config:build --rerun-tasks --refresh-dependencies
./gradlew :config:publish
echo "config published"

./gradlew :federate-base:build --rerun-tasks --refresh-dependencies
./gradlew :federate-base:publish
echo "federate-base published"

./gradlew :coa:build --rerun-tasks --refresh-dependencies
./gradlew :coa:publish
echo "coa published"

./gradlew :federation-manager:build --rerun-tasks --refresh-dependencies
./gradlew :federation-manager:publish
echo "federation-manager published"

./gradlew :fedmanager-host:build --rerun-tasks --refresh-dependencies
./gradlew :fedmanager-host:publish
echo "fedmanager-host published"

# run the HelloWorldJava example
cd /home/cpswt/cpswt-core/examples/HelloWorldJava
gradle wrapper --gradle-version=8.0
./gradlew :Source:build
./gradlew :Sink:build
./gradlew :PingCounter:build
./gradlew :runFederationBatch
