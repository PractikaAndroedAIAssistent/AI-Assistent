package ru.studentai.core.network.interceptor

import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor

/**
 * Создаёт [HttpLoggingInterceptor] с уровнем по флагу [NetworkLoggingFlag].
 *
 * В release-сборке (флаг = false) логируем только базовую информацию: метод, URL, код.
 * В debug — `BODY`, чтобы видеть payload запросов/ответов.
 *
 * Чувствительные заголовки (`Authorization`) намеренно редактируются на `***` через
 * [HttpLoggingInterceptor.redactHeader] — даже в debug мы не хотим, чтобы JWT-токены
 * попадали в logcat-дампы.
 */
public class LoggingInterceptorFactory @Inject constructor(
    private val flag: NetworkLoggingFlag,
) {
    public fun create(): Interceptor =
        HttpLoggingInterceptor().apply {
            level = if (flag.isEnabled()) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.BASIC
            }
            redactHeader("Authorization")
            redactHeader("Cookie")
            redactHeader("Set-Cookie")
        }
}
