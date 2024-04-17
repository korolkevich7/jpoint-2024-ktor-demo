package org.jugru.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.ratelimit.RateLimit
import io.ktor.server.plugins.ratelimit.RateLimitName
import kotlin.time.Duration.Companion.seconds

fun Application.configureRateLimit() {
    install(RateLimit) {
        register(RateLimiters.create) {
            rateLimiter(limit = 2, refillPeriod = 15.seconds)
        }
    }
}

object RateLimiters{
    val create = RateLimitName("create")
}