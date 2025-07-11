plugins {
    id("java-library")
    alias(libs.plugins.kotlin.jvm)
    id("maven-publish")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "mockwebserver-extensions"

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
    implementation(project(":mockwebserver-interceptor"))
    implementation(project(":mockwebserver-request"))
    implementation(libs.mockwebserver)
    implementation(libs.strikt.mockk)

    testImplementation(project(":mockwebserver-assertions"))
    testImplementation(libs.mockk)
    testImplementation(libs.junit)
}
