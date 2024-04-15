package org.jugru

import io.ktor.server.application.Application
import org.jugru.plugins.configureDatabases
import org.jugru.plugins.configureHTTP
import org.jugru.plugins.configureMonitoring
import org.jugru.plugins.configureRouting
import org.jugru.plugins.configureSecurity
import org.jugru.plugins.configureSerialization
import org.jugru.plugins.configureSockets

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}
fun Application.module() {
    configureSockets()
    configureSerialization()
    configureDatabases()
    configureMonitoring()
    configureHTTP()
    configureSecurity()
    configureRouting()
}
