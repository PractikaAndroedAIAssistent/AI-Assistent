package ru.studentai.feature.auth.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class LoginRequestDto(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
)
