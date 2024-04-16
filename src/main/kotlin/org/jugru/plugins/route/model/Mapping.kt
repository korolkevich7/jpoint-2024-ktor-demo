package org.jugru.plugins.route.model

import org.jugru.plugins.SpeakerEntity

fun SpeakerEntity.toModel() =
    SpeakerDTO(
        id = id,
        firstName = firstName,
        lastName = lastName,
        age = age,
        description = description,
    )

fun SpeakerDTO.fromModel() =
    SpeakerEntity(
        id = id,
        firstName = firstName,
        lastName = lastName,
        age = age,
        description = description,
    )