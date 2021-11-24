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

    implementation(files("$rtiHome/lib/portico.jar"))

    implementation(group = "joda-time", name = "joda-time", version = "2.10.10")
    implementation(group="com.fasterxml.jackson.core", name="jackson-annotations", version="2.13.0")

    implementation(group = "org.cpswt", name = "coa", version = "0.7.0-SNAPSHOT")
    implementation(group = "org.cpswt", name = "config", version = "0.7.0-SNAPSHOT")
    implementation(group = "org.cpswt", name = "utils", version = "0.7.0-SNAPSHOT")
    implementation(group = "org.cpswt", name = "federate-base", version = "0.7.0-SNAPSHOT")
    implementation(group = "org.cpswt", name = "root", version = "0.7.0-SNAPSHOT")
    implementation(group = "org.cpswt", name = "base-events", version = "0.7.0-SNAPSHOT")

//    implementation(group = "com.fasterxml.jackson.core", name = "jackson-annotations", version = "2.12.5")
}

tasks.named<JavaExec>("run") {
    mainClass.set("org.cpswt.hla.FederationManager")
//    args = listOf("--configFile", "/home/vagrant/cpswt/cpswt-cpp/Docker/Federation/federateManagerConfig.json")
}

publishing {
    publications {
        create<MavenPublication>("federation-manager") {
            groupId = "org.cpswt"
            artifactId = "federation-manager"
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "federation-managerPublish"
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
