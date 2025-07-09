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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "com.github.appunite.MockWebServer-Extensions"
            artifactId = "mockwebserver-allow-mocking"
            version = libs.versions.projectVersion.get()

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
