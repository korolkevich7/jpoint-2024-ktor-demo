plugins {
    application
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("io.ktor.plugin")
    id("org.jetbrains.compose")
}

group = "org.jugru"
version = "0.0.1"

application {
    mainClass.set("org.jugru.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    google()
}

kotlin {
    jvmToolchain(21)
    jvm {
        withJava()
    }
    js {
        browser {
            useCommonJs()
            commonWebpackConfig {
                outputFileName = "frontend.js"
                cssSupport{
                    enabled = true
                }
                scssSupport {
                    enabled = true
                }
            }
            binaries.executable()
        }
    }
    sourceSets {

        commonMain.dependencies {
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(compose.runtime)
        }

        jvmMain.dependencies {
            implementation(libs.ktor.server.core.jvm)
            implementation(libs.ktor.server.content.negotiation.jvm)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.server.call.logging.jvm)
            implementation(libs.ktor.server.cors)
            implementation(libs.ktor.server.host.common.jvm)
            implementation(libs.ktor.server.status.pages.jvm)
            implementation(libs.ktor.server.netty.jvm)
            implementation(libs.ktor.server.websockets)
            implementation(libs.ktor.server.rate.limit)
            implementation(libs.ktor.server.auth.jvm)

            implementation(libs.logback.classic)

            implementation(JetBrains.exposed.core)
            implementation(JetBrains.exposed.jdbc)
            implementation(JetBrains.exposed.dao)
            implementation(libs.exposed.kotlin.datetime)

            //    postgres
            //    implementation(libs.postgres)

            //    h2 database
            implementation(libs.h2)
        }

        jvmTest.dependencies {
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.server.tests.jvm)
            implementation(libs.kotlin.test.junit)
        }

        jsMain.dependencies {
            implementation(compose.html.core)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.js)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.websockets)
        }

    }
}

// for full-stack
tasks.named<Copy>("jvmProcessResources") {
    val jsBrowserDistribution = tasks.named("jsBrowserDistribution")//"jsBrowserDevelopmentExecutableDistribution"
    dependsOn(jsBrowserDistribution)
    from(jsBrowserDistribution){
        include("*.js")
        include("*.js.map")
        include("*.html")
    }
}


ktor {
    docker {
        jreVersion.set(JavaVersion.VERSION_21)
        localImageName.set("jpoint-2024-ktor")
        imageTag.set(version.toString())
    }
}