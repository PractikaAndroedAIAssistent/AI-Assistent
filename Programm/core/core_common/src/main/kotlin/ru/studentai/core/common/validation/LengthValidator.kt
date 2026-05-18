package ru.studentai.core.common.validation

/**
 * Валидатор длины строки. Длина измеряется в Unicode-codepoint'ах
 * (через `codePointCount`), чтобы эмодзи и редкие символы считались как 1.
 *
 * @param field имя поля для [ValidationError.field]
 * @param min   минимальная допустимая длина включительно (0 = без нижней границы)
 * @param max   максимальная допустимая длина включительно (null = без верхней границы)
 */
public class LengthValidator(
    private val field: String,
    private val min: Int = 0,
    private val max: Int? = null,
) : Validator<String> {

    init {
        require(min >= 0) { "min must be >= 0, got $min" }
        require(max == null || max >= min) { "max ($max) must be >= min ($min)" }
    }

    override fun validate(value: String): ValidationResult {
        val length = value.codePointCount(0, value.length)
        return when {
            length < min -> ValidationResult.invalid(
                field, CODE_TOO_SHORT, "Field '$field' must be at least $min characters (was $length)",
            )
            max != null && length > max -> ValidationResult.invalid(
                field, CODE_TOO_LONG, "Field '$field' must be at most $max characters (was $length)",
            )
            else -> ValidationResult.Valid
        }
    }

    public companion object {
        public const val CODE_TOO_SHORT: String = "too_short"
        public const val CODE_TOO_LONG: String = "too_long"
    }
}
