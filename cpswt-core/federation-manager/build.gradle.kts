plugins {
    java
    application
//    `maven-publish`
}

//val archivaUser: String by project
//val archivaPassword: String by project

//val nexusUsernameAdmin: String by project
//val nexusPasswordAdmin: String by project

group = "org.cpswt"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(group = "org.cpswt", name = "coa", version = "0.6.0-SNAPSHOT")
    implementation(group = "org.cpswt", name = "config", version = "0.6.0-SNAPSHOT")
    implementation(group = "org.cpswt", name = "federate-base", version = "0.6.0-SNAPSHOT")

    implementation(group = "org.apache.logging.log4j", name = "log4j-core", version = "2.14.1")
    implementation(group = "joda-time", name = "joda-time", version = "2.10.10")
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-annotations", version = "2.12.5")
}

tasks.named<JavaExec>("run") {
    mainClass.set("org.cpswt.hla.FederationManager")
//    args = listOf("--configFile", "/home/vagrant/cpswt/cpswt-cpp/Docker/Federation/federateManagerConfig.json")
}

//publishing {
//    publications {
//        create<MavenPublication>("federationManager") {
//            groupId = "org.cpswt"
//            artifactId = "federation-manager"
//            from(components["java"])
//        }
//    }
//    repositories {
//        maven {
//            name = "fedmanagerpublish"
//            val internalRepoUrl = "http://cpswtng_archiva:8080/repository/internal"
//            val snapshotsRepoUrl = "http://cpswtng_archiva:8080/repository/snapshots"
//            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else internalRepoUrl)
//
//            logger.info("URL = \"$url\"")
//            isAllowInsecureProtocol = true
//            authentication {
//                create<BasicAuthentication>("basic")
//            }
//            credentials {
//                username = archivaUser
//                password = archivaPassword
//            }
//        }
//    }
//}
