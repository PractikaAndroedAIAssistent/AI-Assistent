package ru.studentai.core.common.error

import ru.studentai.core.common.validation.ValidationError

/**
 * Ошибка валидации пользовательского ввода.
 * Содержит список конкретных нарушений по полям — UI отображает их рядом с полями формы.
 */
public class ValidationException(
    public val errors: List<ValidationError>,
    cause: Throwable? = null,
) : AppException(
    message = errors.joinToString(separator = "; ") { "${it.field}: ${it.code}" },
    cause = cause,
) {
    public companion object {
        /** Удобный фабричный метод для одной ошибки. */
        public fun of(field: String, code: String, message: String): ValidationException =
            ValidationException(listOf(ValidationError(field, code, message)))
    }
}
