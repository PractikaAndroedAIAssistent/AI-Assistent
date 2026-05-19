package ru.studentai.core.network.auth

import javax.inject.Inject
import ru.studentai.core.common.error.AuthException
import ru.studentai.core.common.result.DomainResult

/**
 * Дефолтная реализация [TokenRefresher].
 *
 * Используется до момента, когда feature_auth подключит свою реализацию.
 * Возвращает [AuthException.RefreshFailed] — клиент это интерпретирует как разлогин.
 *
 * Это позволяет ядру `core_network` собираться и работать без feature_auth (например, в тестах).
 */
public class NoOpTokenRefresher @Inject constructor() : TokenRefresher {
    override suspend fun refresh(refreshToken: String): DomainResult<RefreshedTokens> =
        DomainResult.Failure(AuthException.RefreshFailed())
}
