#HelloWorldJava

This is a very simple example that shows how Java federates are integrated as a co-simulation using the CPSWT framework.

This example includes the following federates:
* Source: This federate generates a 'Ping' HLA-interaction periodically.
* Sink: This federate subscribes to the 'Ping' HLA-interaction and it publishes and updates the HLA-object called 'PingCount'. "PingCount" contains an attribute "Count" to keep track of how many "Ping" HLA-interactions have been received.
* PingCounter: This federates subscribes to "PingCount" HLA-object and prints the "Count" whenever the object state is updated.

Here are the software that are needed and supported in order to run the demo:
* OpenJDK 8
* Apache Archiva 2.2.5
* Portico RTI 2.1.0 (https://porticoproject.org/)
* Ubuntu 20.04 (focal)

Here are the steps to reproduce this demo:
* Install OpenJDK
* Set environment variable JAVA_HOME to parent directory of OpenJDK installation (e.g., to /usr/lib/jvm/java-8-openjdk-amd64)
* Download Portico RTI
* Set environment variable RTI_HOME to where portico installed such that $RTI_HOME/lib contains portico.jar.
* Install Apache Archiva
* ...TBD...
