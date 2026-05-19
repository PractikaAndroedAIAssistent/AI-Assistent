package ru.studentai.core.network.auth

import ru.studentai.core.common.result.DomainResult

/**
 * Контракт обновления access-токена через refresh-токен.
 *
 * `core_network` НЕ знает endpoint `/auth/refresh` — это специфика feature_auth.
 * Конкретная реализация инжектится через Hilt @Binds в feature_auth.
 *
 * Контракт:
 *  • при успехе — возвращает новую пару токенов;
 *  • при ошибке — возвращает [DomainResult.Failure] с подходящим [ru.studentai.core.common.error.AppException];
 *  • НЕ должен сам сохранять токены — это ответственность [TokenAuthenticator].
 */
public fun interface TokenRefresher {
    public suspend fun refresh(refreshToken: String): DomainResult<RefreshedTokens>
}

/** Результат успешного обновления. */
public data class RefreshedTokens(
    val accessToken: String,
    val refreshToken: String,
)
