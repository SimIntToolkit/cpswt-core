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
}


