plugins {
    java
    application
    `maven-publish`
}

val rtiHome = System.getenv("RTI_HOME")

val archivaUser: String by project
val archivaPassword: String by project
val version: String by project
val archivaHostId: String by project
val archivaPort: String by project

dependencies {
    implementation(group = "org.apache.logging.log4j", name = "log4j-core", version = "2.14.1")

    implementation(group = "com.typesafe.akka", name = "akka-actor_2.13", version = "2.6.16")
    implementation(group = "com.typesafe.akka", name = "akka-http_2.13", version = "10.2.6")
    implementation(group = "com.typesafe.akka", name = "akka-http-jackson_2.13", version = "10.2.6")

    implementation(group="com.fasterxml.jackson.datatype", name="jackson-datatype-joda", version="2.13.0")

    implementation(files("$rtiHome/lib/portico.jar"))

    implementation(group = "org.cpswt", name = "coa", version = "0.7.0-SNAPSHOT")
    implementation(group = "org.cpswt", name = "config", version = "0.7.0-SNAPSHOT")
    implementation(group = "org.cpswt", name = "federate-base", version = "0.7.0-SNAPSHOT")
    implementation(group = "org.cpswt", name = "federation-manager", version = "0.7.0-SNAPSHOT")

    runtimeOnly(group = "com.typesafe.akka", name = "akka-stream_2.13", version = "2.6.16")
}


tasks.named<JavaExec>("run") {
    mainClass.set("org.cpswt.host.FederationManagerHostApp")
    args = listOf("--configFile", "/home/vagrant/cpswt/cpswt-cpp/Docker/Federation/federateManagerConfig.json")
}


publishing {
    publications {
        create<MavenPublication>("fedmanager-host") {
            groupId = "org.cpswt"
            artifactId = "fedmanager-host"
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "fedmanager-hostPublish"
            val internalRepoUrl = "http://$archivaHostId:$archivaPort/repository/internal"
            val snapshotsRepoUrl = "http://$archivaHostId:$archivaPort/repository/snapshots"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else internalRepoUrl)

            logger.info("URL = \"$url\"")
            isAllowInsecureProtocol = true
            authentication {
                create<BasicAuthentication>("basic")
            }
            credentials {
                username = archivaUser
                password = archivaPassword
            }
        }
    }
}
