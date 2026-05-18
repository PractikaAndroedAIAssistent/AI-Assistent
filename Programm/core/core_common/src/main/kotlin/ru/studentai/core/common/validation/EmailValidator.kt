package ru.studentai.core.common.validation

import ru.studentai.core.common.constants.Patterns

/**
 * Валидатор email-адреса по RFC-совместимому regex'у из [Patterns.EMAIL].
 *
 * Полностью покрывает требования ТЗ §4.2.1: вход по email + пароль.
 * Пустая строка считается невалидной (для строгого требования «обязательно»
 * композируйте с [RequiredFieldValidator]).
 *
 * @param field имя поля для [ValidationError.field] (по умолчанию "email")
 */
public class EmailValidator(
    private val field: String = DEFAULT_FIELD,
) : Validator<String> {

    override fun validate(value: String): ValidationResult {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) {
            return ValidationResult.invalid(field, RequiredFieldValidator.CODE, "Email is required")
        }
        return if (Patterns.EMAIL.matches(trimmed)) {
            ValidationResult.Valid
        } else {
            ValidationResult.invalid(field, CODE_INVALID_FORMAT, "Email format is invalid")
        }
    }

    public companion object {
        public const val DEFAULT_FIELD: String = "email"
        public const val CODE_INVALID_FORMAT: String = "invalid_format"
    }
}
