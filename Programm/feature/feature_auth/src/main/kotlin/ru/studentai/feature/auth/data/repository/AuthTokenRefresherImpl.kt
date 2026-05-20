package ru.studentai.feature.auth.data.repository

import javax.inject.Inject
import ru.studentai.core.common.result.DomainResult
import ru.studentai.core.network.auth.RefreshedTokens
import ru.studentai.core.network.auth.TokenRefresher
import ru.studentai.core.network.error.SafeApiCall
import ru.studentai.feature.auth.data.remote.api.AuthApi
import ru.studentai.feature.auth.data.remote.dto.RefreshRequestDto

/**
 * Реализация контракта [TokenRefresher] из `core_network`.
 *
 * Вызывает `POST /auth/refresh` (помечен [ru.studentai.core.network.auth.NoAuth] —
 * рефреш не должен сам зацикливаться при 401).
 *
 * Сохранение результата делает [ru.studentai.core.network.auth.TokenAuthenticator]
 * (а не этот класс) — контракт `TokenRefresher` явно это требует.
 */
public class AuthTokenRefresherImpl @Inject constructor(
    private val api: AuthApi,
    private val safeApiCall: SafeApiCall,
) : TokenRefresher {

    override suspend fun refresh(refreshToken: String): DomainResult<RefreshedTokens> {
        val response = safeApiCall { api.refresh(RefreshRequestDto(refreshToken)) }
        return when (response) {
            is DomainResult.Success -> DomainResult.Success(
                RefreshedTokens(
                    accessToken = response.value.accessToken,
                    refreshToken = response.value.refreshToken,
                ),
            )
            is DomainResult.Failure -> response
        }
    }
}
