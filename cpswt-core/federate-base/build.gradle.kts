plugins {
    java
    `maven-publish`
}

group = "isis.vanderbilt.edu"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(group = "org.apache.logging.log4j", name = "log4j-core", version = "2.14.1")

    implementation(files("/home/vagrant/portico-2.1.0/lib/portico.jar"))

    implementation(group = "org.cpswt", name = "utils", version = "0.6.0-SNAPSHOT")
    implementation(group = "org.cpswt", name = "config", version = "0.6.0-SNAPSHOT")
    implementation(group = "org.cpswt", name = "root", version = "0.6.0-SNAPSHOT")
    implementation(group = "org.cpswt", name = "base-events", version = "0.6.0-SNAPSHOT")
}

publishing {
    publications {
        create<MavenPublication>("federateBase") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = uri("http://cpswtng_archiva:8080/repositories/internal")
        }
    }
}