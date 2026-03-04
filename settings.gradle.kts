rootProject.name = "ParcelView"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":androidApp")
include(":composeApp")

file("feature").listFiles()
    ?.filter { it.isDirectory }
    ?.forEach { feature ->
        feature.listFiles()
            ?.filter {
                it.isDirectory && it.name in listOf("public", "impl") // add submodule names here
            }
            ?.forEach { module ->
                include(":feature:${feature.name}:${module.name}")
            }
    }
