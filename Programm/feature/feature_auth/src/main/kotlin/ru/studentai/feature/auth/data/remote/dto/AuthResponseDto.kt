package ru.studentai.feature.auth.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Ответ сервера на login/register — содержит токены и профиль одним пакетом,
 * чтобы клиент не делал дополнительный запрос за профилем сразу после auth.
 */
@Serializable
public data class AuthResponseDto(
    @SerialName("tokens") val tokens: TokensDto,
    @SerialName("user") val user: UserDto,
)
