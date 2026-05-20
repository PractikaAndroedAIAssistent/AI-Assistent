package ru.studentai.feature.auth.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class RefreshRequestDto(
    @SerialName("refreshToken") val refreshToken: String,
)
