rootProject.name = "cpswt-core"

val archivaHostId: String by settings
val archivaPort: String by settings

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    plugins {
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()

        maven {
            isAllowInsecureProtocol = true
            url = uri("http://$archivaHostId:$archivaPort/repository/snapshots")
        }
    }
}

include("base-events")
include("coa")
include("config")
include("federate-base")
include("federation-manager")
include("fedmanager-exec")
include("fedmanager-host")
include("root")
include("utils")
