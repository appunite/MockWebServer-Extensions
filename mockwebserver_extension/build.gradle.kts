plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.okhttp3:mockwebserver:4.12.0")

    implementation("io.mockk:mockk:1.13.5")

    implementation("io.strikt:strikt-core:0.34.1")
    implementation("io.strikt:strikt-mockk:0.34.1")

    testImplementation("junit:junit:4.13.2")
}
