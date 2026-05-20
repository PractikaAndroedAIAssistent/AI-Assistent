package ru.studentai.feature.auth.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class RegisterRequestDto(
    @SerialName("fullName") val fullName: String,
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("role") val role: String,
    @SerialName("university") val university: String? = null,
    @SerialName("group") val group: String? = null,
    @SerialName("course") val course: Int? = null,
    @SerialName("speciality") val speciality: String? = null,
)
