package ir.farhangi.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class Gender {
    MALE,
    FEMALE,
}

fun Gender.persianLabel(): String = when (this) {
    Gender.MALE -> "مرد"
    Gender.FEMALE -> "زن"
}
