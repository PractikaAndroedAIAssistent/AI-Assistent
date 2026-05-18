package ru.studentai.core.common.validation

/**
 * Одна конкретная ошибка валидации.
 *
 * @param field   логическое имя поля формы (например, "email", "password")
 * @param code    машино-читаемый код (например, "empty", "too_short", "invalid_format")
 * @param message человеко-читаемое сообщение (английский — для логов; локализация в UI)
 */
public data class ValidationError(
    val field: String,
    val code: String,
    val message: String,
)
