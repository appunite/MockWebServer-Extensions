plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
    id("maven-publish")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.appunite.MockWebServer-Extensions"
            artifactId = "mockwebserver-extensions"
            version = "0.2.0"

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

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":mockwebserver-interceptor"))
    implementation(project(":mockwebserver-request"))
    implementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    implementation("io.strikt:strikt-mockk:0.34.1")

    testImplementation(project(":mockwebserver-assertions"))
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("junit:junit:4.13.2")
}
