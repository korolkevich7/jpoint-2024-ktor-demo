package org.jugru.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.websocket.sendSerialized
import io.ktor.server.websocket.webSocket
import kotlinx.coroutines.delay
import org.jetbrains.exposed.sql.Database
import org.jugru.plugins.route.model.SpeakerDTO
import org.jugru.plugins.route.model.fromModel
import org.jugru.plugins.route.model.toModel

fun Application.configureDatabases() {
    val database = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        user = "root",
        driver = "org.h2.Driver",
        password = ""
    )
    val speakerService = SpeakerService(database)

    routing {
        route(API_URL) {
            route("/v3/speakers") {
                routingV3(speakerService)
            }
        }
    }
}

private fun Route.routingV3(speakerService: SpeakerService) {
    webSocket {
        val cacheIds = mutableSetOf<Int>()
        val startSpeakers = speakerService.readAll().map { it.toModel() }
        println(startSpeakers)
        startSpeakers.forEach { it.id?.let { it1 -> cacheIds.add(it1) } }
        startSpeakers.forEach { sendSerialized(it)}
        while (closeReason.isActive) {
            val speakers = speakerService.readAll().map { it.toModel() }
            speakers.filter { cacheIds.contains(it.id).not() }.forEach { speaker ->
                sendSerialized(speaker)
                speaker.id?.let { cacheIds.add(it) }
            }
            delay(1000)
        }
    }
    // Create user
    post {
        val user = call.receive<SpeakerDTO>().fromModel()
        val id = speakerService.create(user)
        call.respond(HttpStatusCode.Created, id)
    }

    // Read user
    get("{id}") {
        val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
        val user = speakerService.read(id)
        if (user != null) {
            call.respond(HttpStatusCode.OK, user)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }

    // Update user
    put("{id}") {
        val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
        val user = call.receive<SpeakerDTO>().fromModel()
        speakerService.update(id, user)
        call.respond(HttpStatusCode.OK)
    }

    // Delete user
    delete("{id}") {
        val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
        speakerService.delete(id)
        call.respond(HttpStatusCode.OK)
    }
}
