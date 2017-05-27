# RESTful Federation Manager 

This document explains the differences between the legacy and the "new" RESTful Federation Manager.

## Introduction

The foundation code that supports the RESTful Federation manager is now labeled as version 0.5.0-SNAPSHOT.


## Running FederationManager

In `fedmanager-exec/pom.xml` the `RESTfulFedMgr` profile is defined. The second argument (`fedmgrconfig.yml`) must point to valid config file.


```xml
<profile>
    <id>RESTfulFedMgr</id>
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
                    <mainClass>c2w.host.FederationManagerHostApplication</mainClass>
                    <classpathScope>runtime</classpathScope>
                    <systemProperties>
                        <systemProperty>
                            <key>java.net.preferIPv4Stack</key>
                            <value>true</value>
                        </systemProperty>
                    </systemProperties>
                    <arguments>
                        <argument>server</argument>
                        <argument>fedmgrconfig.yml</argument>
                    </arguments>
                </configuration>
            </plugin>
        </plugins>
    </build>
</profile>
```

To run the federation manager `RTI_RID_FILE` environment variable should be set.

```bash
RTI_RID_FILE=/path/to/RTI.rid mvn exec:java -U -X -P RESTfulFedMgr
```

Current version of Federation Manager starts up, and stays in the `INITIALIZED` state. You can get the current state of the federation manager with the following command:

```bash
curl http://localhost:8083/api/fedmgr
```

There are so-called **control** messages that can be `POST`ed to the Federation Manager:

```bash
# to start - creates the federation and waits for federates to join:
curl -i -X POST http://127.0.0.1:8083/api/fedmgr --data '{"action": "START"}' -H "Content-Type: application/json"

# to pause:
curl -i -X POST http://127.0.0.1:8083/api/fedmgr --data '{"action": "PAUSE"}' -H "Content-Type: application/json"

# to resume:
curl -i -X POST http://127.0.0.1:8083/api/fedmgr --data '{"action": "RESUME"}' -H "Content-Type: application/json"

# to terminate:
curl -i -X POST http://127.0.0.1:8083/api/fedmgr --data '{"action": "TERMINATE"}' -H "Content-Type: application/json"
```

## Running a Federate

In our example we use the `c2w-examples-structured` project from `c2wtng-java` where we create the federation named _HelloWorld_Java_Tutorial_.

The folder structure is the following:
 
  * **HelloWorld_Java_Tutorial_1_FederatesExporter** - the output of the FederatesExporter plugin
  * **HelloWorld_Java_Tutorial_2_Implementation** - the custom implementation
  * **HelloWorld_Java_Tutorial_3_DeploymentExporter** - the output of the DeploymentExporter plugin
  * **support_files** - files for the docker execution (experimental)

To run a federate `RTI_RID_FILE` environment variable should be set.

```bash
RTI_RID_FILE=/path/to/RTI.rid mvn exec:exec -U -X -P Source,ExecExec
```


