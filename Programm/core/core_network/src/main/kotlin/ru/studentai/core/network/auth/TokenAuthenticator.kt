package ru.studentai.core.network.auth

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Invocation
import ru.studentai.core.common.logger.Logger
import ru.studentai.core.common.logger.NoOpLogger
import ru.studentai.core.common.result.DomainResult

/**
 * OkHttp-Authenticator: при 401-ответе пытается обновить токен и повторить запрос.
 *
 * ## Поведение
 * 1. Если запрос помечен [NoAuth] — рефреш не делаем (это и есть refresh/login endpoint).
 * 2. Если у запроса уже есть header [HEADER_RETRY] — это повторная попытка после refresh,
 *    которая снова дала 401 — выходим (избегаем бесконечного цикла).
 * 3. Достаём refresh-токен. Если его нет — Unauthenticated, выходим.
 * 4. Под [Mutex] вызываем [TokenRefresher.refresh]. Mutex гарантирует, что параллельные
 *    401 не запустят несколько refresh'ей: второй поток дождётся и возьмёт уже обновлённый
 *    access token.
 * 5. При успехе — сохраняем новые токены и пересоздаём запрос с новым access token + retry header.
 * 6. При ошибке — очищаем хранилище (logout) и возвращаем null (OkHttp пробросит 401 наверх).
 *
 * ## Почему `runBlocking`
 * OkHttp Authenticator API синхронный (intercept happens on a worker thread). Все suspend
 * операции внутри [tokenStorage] и [tokenRefresher] делегируются на свои dispatcher'ы,
 * поэтому реального блокирования main-потока нет.
 */
@Singleton
public class TokenAuthenticator @Inject constructor(
    private val tokenStorage: AuthTokenStorage,
    private val tokenRefresher: TokenRefresher,
    private val logger: Logger = NoOpLogger,
) : Authenticator {

    private val refreshMutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        val originalRequest = response.request

        if (isNoAuthRequest(originalRequest)) return null
        if (originalRequest.header(HEADER_RETRY) != null) {
            logger.warn(TAG, "Already retried after refresh — giving up.")
            return null
        }

        return runBlocking {
            refreshMutex.withLock {
                // После получения мьютекса другой поток мог уже обновить токен.
                // Сравним access-токен в текущем заголовке с тем, что в хранилище.
                val currentTokenInRequest = originalRequest
                    .header(AuthInterceptor.HEADER_AUTHORIZATION)
                    ?.removePrefix(AuthInterceptor.BEARER_PREFIX)
                val freshTokenInStorage = tokenStorage.getAccessToken()
                if (
                    freshTokenInStorage != null &&
                    freshTokenInStorage != currentTokenInRequest
                ) {
                    return@withLock rebuildRequest(originalRequest, freshTokenInStorage)
                }

                val refreshToken = tokenStorage.getRefreshToken() ?: run {
                    logger.warn(TAG, "No refresh token — cannot recover from 401.")
                    return@withLock null
                }
                when (val result = tokenRefresher.refresh(refreshToken)) {
                    is DomainResult.Success -> {
                        tokenStorage.saveTokens(
                            accessToken = result.value.accessToken,
                            refreshToken = result.value.refreshToken,
                        )
                        rebuildRequest(originalRequest, result.value.accessToken)
                    }
                    is DomainResult.Failure -> {
                        logger.error(TAG, "Refresh failed: ${result.error.message}", result.error)
                        tokenStorage.clear()
                        null
                    }
                }
            }
        }
    }

    private fun rebuildRequest(original: Request, newAccessToken: String): Request =
        original.newBuilder()
            .header(
                AuthInterceptor.HEADER_AUTHORIZATION,
                "${AuthInterceptor.BEARER_PREFIX}$newAccessToken",
            )
            .header(HEADER_RETRY, "true")
            .build()

    private fun isNoAuthRequest(request: Request): Boolean {
        val invocation = request.tag(Invocation::class.java) ?: return false
        return invocation.method().isAnnotationPresent(NoAuth::class.java)
    }

    internal companion object {
        const val TAG = "TokenAuthenticator"
        const val HEADER_RETRY = "X-Auth-Retried"
    }
}
