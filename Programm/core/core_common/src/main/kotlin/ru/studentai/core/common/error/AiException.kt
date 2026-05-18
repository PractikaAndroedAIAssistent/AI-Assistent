package ru.studentai.core.common.error

/**
 * Ошибки слоя AI (см. ТЗ §4.2.7).
 * Особенно важно: при отсутствии информации в материале — приложение
 * должно сообщать [NoDataForQuery], а не выдавать выдуманный ответ.
 */
public sealed class AiException(
    message: String? = null,
    cause: Throwable? = null,
) : AppException(message, cause) {

    /** В выбранном материале нет данных для ответа на запрос пользователя. */
    public class NoDataForQuery(
        public val query: String,
        cause: Throwable? = null,
    ) : AiException("No relevant data in source material for query: $query", cause)

    /** Превышен лимит токенов запроса/ответа. */
    public class ContextLimitExceeded(
        public val tokens: Int,
        public val limit: Int,
        cause: Throwable? = null,
    ) : AiException("Context limit exceeded: $tokens > $limit", cause)

    /** Лимит запросов к провайдеру (rate limit). */
    public class RateLimited(
        public val retryAfterSeconds: Long? = null,
        cause: Throwable? = null,
    ) : AiException(
        retryAfterSeconds?.let { "Rate limited, retry after ${it}s" } ?: "Rate limited",
        cause,
    )

    /** LLM-провайдер вернул ошибку (бизнес-уровня). */
    public class ProviderError(
        public val providerName: String,
        message: String? = null,
        cause: Throwable? = null,
    ) : AiException("AI provider '$providerName' error: ${message ?: "unknown"}", cause)

    /** Запрошенная модель недоступна. */
    public class ModelUnavailable(
        public val model: String,
        cause: Throwable? = null,
    ) : AiException("AI model '$model' unavailable", cause)

    /** Ответ модели не прошёл валидацию (формат, схема). */
    public class InvalidResponse(
        message: String? = null,
        cause: Throwable? = null,
    ) : AiException(message ?: "AI response is invalid", cause)
}
