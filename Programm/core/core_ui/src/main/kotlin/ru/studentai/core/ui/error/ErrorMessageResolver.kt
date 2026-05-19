package ru.studentai.core.ui.error

import ru.studentai.core.common.error.AppException

/**
 * Маппер `AppException` → человеко-читаемый текст для UI.
 *
 * Контракт:
 *  • никогда не бросает исключения;
 *  • для каждого подтипа [AppException] возвращает локализованную строку;
 *  • для неизвестных подтипов — возвращает «непредвиденная ошибка».
 *
 * Точка расширения: feature-модули могут реализовать свой `ErrorMessageResolver`
 * (например, специализированный для AI-фичи) и переопределить биндинг в Hilt.
 * Дефолтная реализация — [DefaultErrorMessageResolver].
 */
public fun interface ErrorMessageResolver {
    public fun resolve(error: AppException): String
}
