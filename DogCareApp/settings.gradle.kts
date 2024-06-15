pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        jcenter() // Warning: this repository is going to shut down soon
        maven { url = uri( "https://www.jitpack.io")}
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter() /* Warning: this repository is going to shut down soon */
        maven { url = uri( "https://www.jitpack.io")}
    }
}

rootProject.name = "DogCareApp"
include(":app")
 