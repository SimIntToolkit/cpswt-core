plugins {
    java
    application
    `maven-publish`
}

group = "isis.vanderbilt.edu"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(group = "com.typesafe.akka", name = "akka-actor_2.13", version = "2.6.16")
    implementation(group = "com.typesafe.akka", name = "akka-http_2.13", version = "10.2.6")
    implementation(group = "com.typesafe.akka", name = "akka-http-jackson_2.13", version = "10.2.6")

    implementation(files("/home/vagrant/portico-2.1.0/lib/portico.jar"))

    implementation(group = "org.cpswt", name = "config", version = "0.6.0-SNAPSHOT")
    implementation(group = "org.cpswt", name = "federate-base", version = "0.6.0-SNAPSHOT")
    implementation(group = "org.cpswt", name = "federation-manager", version = "0.6.0-SNAPSHOT")

    runtimeOnly(group = "com.typesafe.akka", name = "akka-stream_2.13", version = "2.6.16")
}

tasks.named<JavaExec>("run") {
    mainClass.set("org.cpswt.host.FederationManagerHostApp")
    args = listOf("--configFile", "/home/vagrant/cpswt/cpswt-cpp/Docker/Federation/federateManagerConfig.json")
}

