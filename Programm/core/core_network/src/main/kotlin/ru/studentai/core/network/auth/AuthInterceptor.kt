package ru.studentai.core.network.auth

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.Invocation

/**
 * OkHttp-интерсептор, добавляющий `Authorization: Bearer <accessToken>` ко всем запросам,
 * кроме помеченных [NoAuth].
 *
 * Замечания по производительности:
 *  • Получение токена — suspend-операция (читаем из EncryptedSharedPreferences через IO);
 *    интерсептор синхронен по контракту OkHttp, поэтому используем `runBlocking` строго
 *    для одной короткой операции чтения. Реальная I/O идёт в IO-dispatcher'е внутри
 *    [AuthTokenStorage], поэтому main-поток не блокируется (интерсептор уже на OkHttp-thread).
 *  • Если токена нет (Unauthenticated сессия) — запрос идёт без заголовка; ответственность
 *    за обработку 401 — у [TokenAuthenticator].
 */
@Singleton
public class AuthInterceptor @Inject constructor(
    private val tokenStorage: AuthTokenStorage,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (isNoAuthEndpoint(request)) {
            return chain.proceed(request)
        }

        val token = runBlocking { tokenStorage.getAccessToken() }
            ?: return chain.proceed(request)

        val authorized = request.newBuilder()
            .header(HEADER_AUTHORIZATION, "$BEARER_PREFIX$token")
            .build()
        return chain.proceed(authorized)
    }

    private fun isNoAuthEndpoint(request: Request): Boolean {
        val invocation = request.tag(Invocation::class.java) ?: return false
        return invocation.method().isAnnotationPresent(NoAuth::class.java)
    }

    internal companion object {
        const val HEADER_AUTHORIZATION = "Authorization"
        const val BEARER_PREFIX = "Bearer "
    }
}
