package ru.studentai.core.common.validation

/**
 * Валидатор «поле обязательно». Считает пустые/whitespace-строки невалидными.
 *
 * @param field имя поля для [ValidationError.field]
 */
public class RequiredFieldValidator(
    private val field: String,
) : Validator<String?> {

    override fun validate(value: String?): ValidationResult {
        val isEmpty = value.isNullOrBlank()
        return if (isEmpty) {
            ValidationResult.invalid(field, CODE, "Field '$field' is required")
        } else {
            ValidationResult.Valid
        }
    }

    public companion object {
        public const val CODE: String = "required"
    }
}
