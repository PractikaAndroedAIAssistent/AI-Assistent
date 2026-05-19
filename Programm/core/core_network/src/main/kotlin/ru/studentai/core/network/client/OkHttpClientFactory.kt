package ru.studentai.core.network.client

import java.util.concurrent.TimeUnit
import javax.inject.Inject
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import ru.studentai.core.network.auth.AuthInterceptor
import ru.studentai.core.network.auth.TokenAuthenticator
import ru.studentai.core.network.interceptor.LoggingInterceptorFactory

/**
 * Создаёт production-готовый [OkHttpClient].
 *
 * Параметры по умолчанию:
 *  • connectTimeout / readTimeout / writeTimeout — 30 секунд (баланс между UX и flaky сетью);
 *  • retryOnConnectionFailure — `true` (OkHttp сам ретраит на транспортных сбоях);
 *  • callTimeout — 60 секунд (общий лимит вызова).
 *
 * Подключаются:
 *  1. [AuthInterceptor] — добавляет Bearer-токен;
 *  2. [LoggingInterceptorFactory] — логирование (только debug-уровень body);
 *  3. [TokenAuthenticator] — рефреш при 401.
 *
 * Дополнительные интерсепторы можно передать в [extraInterceptors] / [extraNetworkInterceptors].
 */
public class OkHttpClientFactory @Inject constructor(
    private val authInterceptor: AuthInterceptor,
    private val tokenAuthenticator: TokenAuthenticator,
    private val loggingFactory: LoggingInterceptorFactory,
) {

    public fun create(
        extraInterceptors: List<Interceptor> = emptyList(),
        extraNetworkInterceptors: List<Interceptor> = emptyList(),
        authenticator: Authenticator = tokenAuthenticator,
        connectTimeoutSeconds: Long = DEFAULT_CONNECT_TIMEOUT_SECONDS,
        readTimeoutSeconds: Long = DEFAULT_READ_TIMEOUT_SECONDS,
        writeTimeoutSeconds: Long = DEFAULT_WRITE_TIMEOUT_SECONDS,
        callTimeoutSeconds: Long = DEFAULT_CALL_TIMEOUT_SECONDS,
    ): OkHttpClient {
        return OkHttpClient.Builder().apply {
            connectTimeout(connectTimeoutSeconds, TimeUnit.SECONDS)
            readTimeout(readTimeoutSeconds, TimeUnit.SECONDS)
            writeTimeout(writeTimeoutSeconds, TimeUnit.SECONDS)
            callTimeout(callTimeoutSeconds, TimeUnit.SECONDS)
            retryOnConnectionFailure(true)
            addInterceptor(authInterceptor)
            extraInterceptors.forEach(::addInterceptor)
            addInterceptor(loggingFactory.create())
            extraNetworkInterceptors.forEach(::addNetworkInterceptor)
            authenticator(authenticator)
        }.build()
    }

    public companion object {
        public const val DEFAULT_CONNECT_TIMEOUT_SECONDS: Long = 30L
        public const val DEFAULT_READ_TIMEOUT_SECONDS: Long = 30L
        public const val DEFAULT_WRITE_TIMEOUT_SECONDS: Long = 30L
        public const val DEFAULT_CALL_TIMEOUT_SECONDS: Long = 60L
    }
}
