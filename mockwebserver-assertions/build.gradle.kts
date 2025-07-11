plugins {
    id("java-library")
    alias(libs.plugins.kotlin.jvm)
    id("maven-publish")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.appunite.MockWebServer-Extensions"
            artifactId = "mockwebserver-assertions"
            version = libs.versions.projectVersion.get()

            afterEvaluate {
                from(components["java"])
            }
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(project(":mockwebserver-request"))
    implementation(libs.okhttp)
    implementation(libs.strikt.mockk)
}
