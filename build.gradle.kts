// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.android.library) apply false
}

// Configure Java toolchain for all projects
allprojects {
    // Configure Kotlin compiler options
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget(libs.versions.javaVersion.get()))
        }
    }

    plugins.withId("maven-publish") {
        configure<PublishingExtension> {
            // Set version for all publications
            publications.withType<MavenPublication> {
                groupId = "com.github.appunite.MockWebServer-Extensions"
                version = System.getenv("version").orEmpty().ifEmpty { "0.0.1-SNAPSHOT" }
            }
        }
    }
}
