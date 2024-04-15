
val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("io.ktor.plugin")
}

group = "jugru.org"
version = "0.0.1"

application {
    mainClass.set("jugru.org.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktor.server.core.jvm)
    implementation(libs.ktor.server.content.negotiation.jvm)
    implementation(libs.ktor.serialization.kotlinx.json.jvm)
    implementation(libs.ktor.server.call.logging.jvm)
    implementation(libs.ktor.server.cors.jvm)
    implementation(libs.ktor.server.host.common.jvm)
    implementation(libs.ktor.server.status.pages.jvm)
    implementation(libs.ktor.server.netty.jvm)
    implementation(libs.ktor.server.websockets.jvm)
    implementation(libs.ktor.server.rate.limit)
    implementation(libs.ktor.server.auth.jvm)

    implementation(libs.logback.classic)

    implementation(JetBrains.exposed.core)
    implementation(JetBrains.exposed.jdbc)
    implementation(JetBrains.exposed.dao)
    implementation(libs.exposed.kotlin.datetime)

    implementation(libs.h2)

    testImplementation(libs.ktor.server.content.negotiation.jvm)
    testImplementation(libs.ktor.server.tests.jvm)
    testImplementation(Kotlin.test.junit)
}
