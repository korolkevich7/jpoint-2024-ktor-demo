package org.jugru.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.sendSerialized
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import org.jugru.plugins.route.model.SpeakerDTO

const val API_URL = "/jpoint-demo/api"
fun Application.configureRouting() {
    exceptionHandling()

    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
        pingPeriod = 15.seconds.toJavaDuration()
        timeout = 15.seconds.toJavaDuration()
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        route(API_URL) {
            routeV1()
            routeV2()
        }
    }
}

private fun Application.exceptionHandling() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }
}

fun Route.routeV2() {
    route("v2/speakers") {
        readSpeaker()
    }
}

fun Route.readSpeaker() {
    get("{id}") {
        val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("id is required")

        call.respond(
            SpeakerDTO(
                id = id,
                firstName = "John",
                lastName = "Doe",
                description = "JVM Developer",
                age = 30,
            )
        )
    }
}

fun Route.routeV1() {
    route("v1") {
        webSocket("speakers") {
            while (closeReason.isActive) {
                sendSerialized(mapOf("firstName" to "Alexandr", "lastName" to "Nozik"))
                delay(5.seconds)
            }
        }

        get {
            call.respondText("Hello World!")
        }


        get("mockSpeaker") {
            call.respond(mapOf("firstName" to "Alexandr", "lastName" to "Nozik"))
        }
    }
}
