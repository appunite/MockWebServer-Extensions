pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MockWebServer Extensions"
include(":app")
include(":mockwebserver-extensions")
include(":mockwebserver-interceptor")
include(":mockwebserver-allow-mocking")
include(":mockwebserver-assertions")
include(":mockwebserver-request")
