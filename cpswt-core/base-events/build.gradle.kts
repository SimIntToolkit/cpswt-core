plugins {
    java
    `maven-publish`
}

val rtiHome = System.getenv("RTI_HOME")

val archivaUser: String by project
val archivaPassword: String by project
val version: String by project
val archivaHostId: String by project
val archivaPort: String by project


repositories {
    mavenCentral()
    maven {
            isAllowInsecureProtocol = true
        url = uri("http://$archivaHostId:$archivaPort/repository/snapshots")
    }
}

dependencies {
    implementation(group="org.apache.logging.log4j", name="log4j-core", version="2.14.1")

    implementation(files("$rtiHome/lib/portico.jar"))

    implementation(group="org.cpswt", name="utils", version="0.7.0-SNAPSHOT")
    implementation(group="org.cpswt", name="root", version="0.7.0-SNAPSHOT")


    testImplementation(group="junit", name="junit", version="4.13.2")
    testImplementation(group="org.mockito", name="mockito-core", version="4.1.0")
}

publishing {
    publications {
        create<MavenPublication>("base-events") {
            groupId = "org.cpswt"
            artifactId = "base-events"
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "base-eventsPublish"
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