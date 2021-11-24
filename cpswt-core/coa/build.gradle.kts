plugins {
    java
    `maven-publish`
}

//dependencies {
//
//    implementation(group = "org.cpswt", name = "root", version = "0.6.0-SNAPSHOT")
//    implementation(group = "org.cpswt", name = "base-events", version = "0.6.0-SNAPSHOT")
//}

val rtiHome = System.getenv("RTI_HOME")

val archivaUser: String by project
val archivaPassword: String by project
val version: String by project
val archivaHostId: String by project
val archivaPort: String by project

dependencies {
    implementation(group="org.apache.logging.log4j", name="log4j-core", version="2.14.1")

    implementation(group = "com.fasterxml.jackson.core", name = "jackson-annotations", version = "2.12.5")
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = "2.12.5")

    implementation(files("$rtiHome/lib/portico.jar"))

    implementation(group="org.cpswt", name="root", version="0.7.0-SNAPSHOT")
    implementation(group="org.cpswt", name="utils", version="0.7.0-SNAPSHOT")
    implementation(group="org.cpswt", name="base-events", version="0.7.0-SNAPSHOT")
}

publishing {
    publications {
        create<MavenPublication>("coa") {
            groupId = "org.cpswt"
            artifactId = "coa"
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "coaPublish"
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
