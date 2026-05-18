package ru.studentai.core.common.error

/**
 * Все сетевые ошибки. Маппинг IOException/HttpException → [NetworkException]
 * выполняется в `core_network` (Retrofit/OkHttp interceptors).
 */
public sealed class NetworkException(
    message: String? = null,
    cause: Throwable? = null,
) : AppException(message, cause) {

    /** Нет соединения с сетью (нет интернета / в режиме полёта). */
    public class NoConnection(cause: Throwable? = null) :
        NetworkException("No network connection", cause)

    /** Тайм-аут запроса. */
    public class Timeout(cause: Throwable? = null) :
        NetworkException("Network request timed out", cause)

    /**
     * HTTP-ошибка с кодом ответа.
     * [body] — тело ответа, если сервер вернул структурированную ошибку.
     */
    public class Http(
        public val code: Int,
        public val body: String? = null,
        cause: Throwable? = null,
    ) : NetworkException("HTTP $code${body?.let { ": $it" }.orEmpty()}", cause)

    /** 5xx-ошибки сервера. */
    public class Server(
        public val code: Int,
        message: String? = null,
        cause: Throwable? = null,
    ) : NetworkException(message ?: "Server error ($code)", cause)

    /** Ошибка сериализации/десериализации (битый JSON, несоответствие схемы). */
    public class Serialization(
        message: String? = null,
        cause: Throwable? = null,
    ) : NetworkException(message ?: "Serialization error", cause)

    /** Запрос отменён (например, при смене конфигурации). */
    public class Cancelled(cause: Throwable? = null) :
        NetworkException("Request cancelled", cause)
}
