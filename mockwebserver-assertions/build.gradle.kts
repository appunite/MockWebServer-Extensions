plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
    id("maven-publish")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.appunite.MockWebServer-Extensions"
            artifactId = "mockwebserver-assertions"
            version = "0.3.0"

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
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("io.strikt:strikt-mockk:0.34.1")
}
