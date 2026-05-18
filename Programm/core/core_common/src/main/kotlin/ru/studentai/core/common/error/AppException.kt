package ru.studentai.core.common.error

/**
 * Корневой тип всех ошибок домена StudentAI.
 *
 * Все слои выше data возвращают только подтипы [AppException] — никаких "сырых" [Throwable].
 * Любое внешнее исключение должно быть отмаплено в один из подтипов в data-слое
 * (см. mapper'ы в `core_network`, `core_database`, и т. д.).
 *
 * `sealed` иерархия гарантирует exhaustive `when` в presentation-слое.
 */
public sealed class AppException(
    message: String? = null,
    cause: Throwable? = null,
) : RuntimeException(message, cause)

/**
 * Ошибка, для которой не нашлось более специфичной категории.
 * Должна появляться только как защитный fallback — не использовать как основной путь.
 */
public class UnknownException(
    message: String? = null,
    cause: Throwable? = null,
) : AppException(message ?: "Unknown error", cause)
