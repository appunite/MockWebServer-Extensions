plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "com.appunite.mockwebserver_allow_mocking"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = libs.versions.javaVersion.get()
    }
}

publishing {
    publications {
        create<MavenPublication>("release") {
            artifactId = "mockwebserver-allow-mocking"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
