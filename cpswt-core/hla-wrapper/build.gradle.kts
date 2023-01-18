plugins {
    java
    `maven-publish`
}

group = "isis.vanderbilt.edu"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(files("/home/vagrant/portico-2.1.0/lib/portico.jar"))
}


