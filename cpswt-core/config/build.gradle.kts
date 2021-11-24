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


dependencies {
    implementation(group = "org.apache.logging.log4j", name = "log4j-core", version = "2.14.1")

    implementation(group="com.fasterxml.jackson.core", name="jackson-annotations", version="2.13.0")
    implementation(group="com.fasterxml.jackson.core", name="jackson-core", version="2.13.0")
    implementation(group="com.fasterxml.jackson.core", name="jackson-databind", version="2.13.0")

    implementation(group="commons-cli", name="commons-cli", version="1.5.0")
    implementation(group="org.apache.commons", name="commons-lang3", version="3.12.0")

    implementation(group="org.cpswt", name="utils", version="0.7.0-SNAPSHOT")
}

publishing {
    publications {
        create<MavenPublication>("config") {
            groupId = "org.cpswt"
            artifactId = "config"
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "configPublish"
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
