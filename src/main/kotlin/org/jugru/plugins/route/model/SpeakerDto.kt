package org.jugru.plugins.route.model

import kotlinx.serialization.Serializable

@Serializable
data class SpeakerDTO(
    val id: Int? = null,
    val firstName: String,
    val lastName: String,
    val age: Int,
    val description: String,
)

