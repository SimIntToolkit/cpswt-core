plugins {
    java
    `maven-publish`
}

group = "isis.vanderbilt.edu"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-annotations", version = "2.12.5")
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = "2.12.5")

    implementation(group = "org.cpswt", name = "root", version = "0.6.0-SNAPSHOT")
    implementation(group = "org.cpswt", name = "base-events", version = "0.6.0-SNAPSHOT")
}


