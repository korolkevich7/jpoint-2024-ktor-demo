package org.jugru

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.*
import kotlin.test.*
import org.jugru.plugins.configureRouting

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        environment {
            config = ApplicationConfig("application-test.conf")
        }
        application {
            configureRouting()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }
}
