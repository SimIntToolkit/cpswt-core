<!-- DO NOT MODIFY THIS FILE IN THE cpswt-core PROJECT.
IT WAS GENERATED IN THE cpswt-docs PROJECT.  TO MODIFY, ONLY MODIFY THE "README.rst" FILES
(WHICH ARE reStructuredText) IN THE <cpswt-docs>/DocsMain/ProjectDocs/cpswt-core DIRECTORY,
WHERE <cpswt-docs> IS THE TOP-LEVEL DIRECTORY OF THE cpswt-docs PROJECT.
THEN, IN A TERMINAL, CD TO THE <cpswt-docs>/DocsMain DIRECTORY AND EXECUTE
"./gradlew :ProjectDocs:cpswt-core:build".  ONCE THIS COMMAND FINISHES, CD TO THE TOP-LEVEL
cpswt-core DIRECTORY AND EXECUTE "unzip -oq <cpswt-docs>/DocsMain/ProjectDocs/cpswt-core/Zipfile/cpswt-core.zip" -->

# To Run the HelloWorldJava Example

#### NOTE
Steps below assume that you have installed CPSWT on your computer as shown in
[CPSWT Software Installation and Instructions to Build and Publish cpswt-core Modules on Ubuntu 20.04 LTS (Focal Fossa)](../../README.md#cpswtjavasoftwareinstallation)

* Open a terminal and change directory to the HelloWorldJava directory
  ```bash
  cd <cpswt-core>/examples/HelloWorldJava
  ```

  where <cpswt-core> is the top-level directory of the cpswt-core project on your computer.
* For the first run only, enter the following command:
  ```bash
  gradle wrapper --gradle-version=8.0
  ```
* To run the federation, enter the following command:
  ```bash
  ./gradlew :runFederation
  ```

## Explanation of HelloWorld example

In its CPSWT model, the FOM of the HelloWorld example is shown in the figure below:

![WebGME HelloWorldJava FOM](Images/HelloWorldJavaFOM.png)

The Source federate published the Ping interaction and sends out an infinite sequence of them, printing a message for
each Ping it sends.

The Sink federate subscribes to the Ping interaction and, upon receipt, prints a message to this affect.  It also
publishes a PingCount object that has a single integer attribute pingCount.  It increments pingCount for each Ping
interaction it receives, while also printing the value of pingCount.

The PingCounter federate subscribes to the PingCount object and prints the value of its pingCount attribute
every time it changes.
