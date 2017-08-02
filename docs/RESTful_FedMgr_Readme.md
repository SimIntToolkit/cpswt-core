# RESTful Federation Manager 

This document explains the differences between the legacy and the "new" RESTful Federation Manager.

## Introduction

The foundation code that supports the RESTful Federation manager is now labeled as version 0.5.0-SNAPSHOT.


## Running FederationManager

See working example in `testfiles/scripts/start_fedmanager` and `testfiles/pom.xml`


```xml
<profile>
            <id>FederationManagerExecJavaAKKA</id>
            <build>
                <plugins>
                    <plugin>
                        <groupId>org.codehaus.mojo</groupId>
                        <artifactId>exec-maven-plugin</artifactId>
                        <version>1.5.0</version>
                        <goals>
                            <goal>java</goal>
                        </goals>
                        <configuration>
                            <mainClass>org.cpswt.host.FederationManagerHostApp</mainClass>
                            <classpathScope>runtime</classpathScope>
                            <systemProperties>
                                <systemProperty>
                                    <key>java.net.preferIPv4Stack</key>
                                    <value>true</value>
                                </systemProperty>
                                <systemProperty>
                                    <key>log4j.configurationFile</key>
                                    <value>./log4j2.xml</value>
                                </systemProperty>
                            </systemProperties>
                            <arguments>
                                <argument>-configFile</argument>
                                <argument>fedmgrconfig.json</argument>
                                <argument>-stepSize</argument>
                                <argument>1.0</argument>
                                <argument>-federationEndTime</argument>
                                <argument>0</argument>
                            </arguments>
                        </configuration>
                    </plugin>
                </plugins>
            </build>
        </profile>
```

To run the federation manager `RTI_RID_FILE` environment variable should be set.

```bash
RTI_RID_FILE=/path/to/RTI.rid mvn exec:java -U -X -P FederationManagerExecJavaAKKA
```

Current version of Federation Manager starts up, and stays in the `INITIALIZED` state. You can get the current state of the federation manager with the following command:

```bash
curl http://localhost:8083/fedmgr
```

There are so-called **control** messages that can be `POST`ed to the Federation Manager:

```bash
# to start - creates the federation and waits for federates to join:
curl -i -X POST http://127.0.0.1:8083/fedmgr --data '{"action": "START"}' -H "Content-Type: application/json"

# to pause:
curl -i -X POST http://127.0.0.1:8083/fedmgr --data '{"action": "PAUSE"}' -H "Content-Type: application/json"

# to resume:
curl -i -X POST http://127.0.0.1:8083/fedmgr --data '{"action": "RESUME"}' -H "Content-Type: application/json"

# to terminate:
curl -i -X POST http://127.0.0.1:8083/fedmgr --data '{"action": "TERMINATE"}' -H "Content-Type: application/json"
```

## Running a Federate

See `EchoExample` example:

    * `testfiles/scripts/start_echoclient[late|sync]`
    * `testfiles/scripts/start_echoserver[late|sync]`
    * `testfiles/experimentConfig.json`
    * `testfiles/fedmgrconfig.json`

## Query federates

You can query the FederationManager to provide information about online and resigned federates:

```bash
curl http://127.0.0.1:8083/federates
```