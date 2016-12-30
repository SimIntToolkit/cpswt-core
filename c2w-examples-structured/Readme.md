# c2w-examples-structured

This subproject is the example of a "simple Helloworld" workflow.

To perform these steps, you need access to `cpswtng-meta` repo and the `HelloWorld.webgmex` seed.

## HelloWorld_Java_Tutorial_1_FederatesExporter

This folder is the result of the `Federates Exporter` with the following variables:

```
    version = 0.0.1
    groupId = org.c2wt.example
    org.c2w version = 0.4.0-SNAPSHOT
    archiva repo base url = http://cpswtng_archiva:8080
```

Needed steps: maven clean -> compile -> deploy

## HelloWorld_Java_Tutorial_2_Implementation

This folder is the implementation of the interfaces generated above.

## HelloWorld_Java_Tutorial_3_DeploymentExporter

This folder is the result of the `Deployment Exporter` with the following variables:

```
    version = 0.0.1
    groupId = org.c2wt.example
    org.c2w version = 0.4.0-SNAPSHOT
    archiva repo base url = http://cpswtng_archiva:8080
```