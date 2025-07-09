plugins {
    id("java-library")
    alias(libs.plugins.kotlin.jvm)
    id("maven-publish")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.appunite.MockWebServer-Extensions"
            artifactId = "mockwebserver-extensions"
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
    implementation(project(":mockwebserver-interceptor"))
    implementation(project(":mockwebserver-request"))
    implementation(libs.mockwebserver)
    implementation(libs.strikt.mockk)

    testImplementation(project(":mockwebserver-assertions"))
    testImplementation(libs.mockk)
    testImplementation(libs.junit)
}
