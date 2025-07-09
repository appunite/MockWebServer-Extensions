plugins {
    id("java-library")
    alias(libs.plugins.kotlin.jvm)
    id("maven-publish")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.appunite.MockWebServer-Extensions"
            artifactId = "mockwebserver-request"
            version = libs.versions.projectVersion.get()

            afterEvaluate {
                from(components["java"])
            }
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(libs.okhttp)
}
