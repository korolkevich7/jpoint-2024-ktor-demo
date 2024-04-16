package org.jugru

import io.ktor.server.application.Application
import org.jugru.plugins.configureDatabases
import org.jugru.plugins.configureCors
import org.jugru.plugins.configureMonitoring
import org.jugru.plugins.configureRateLimit
import org.jugru.plugins.configureRouting
import org.jugru.plugins.configureSecurity
import org.jugru.plugins.configureSerialization
import org.jugru.plugins.configureSockets

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}
fun Application.module() {
    configureSerialization()
    configureMonitoring()
    configureCors()
    configureSecurity()
    configureRateLimit()
    configureRouting()
    configureSockets()
    configureDatabases()
}
