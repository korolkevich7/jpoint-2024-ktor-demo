package org.jugru.model

import kotlinx.serialization.Serializable

//Нужно подключить и плагин и зависимость в  build.gradle + подключить плагин Ktor
@Serializable
data class SpeakerDTO(
    val id: Int? = null,
    val firstName: String,
    val lastName: String,
    val age: Int,
    val description: String,
)
