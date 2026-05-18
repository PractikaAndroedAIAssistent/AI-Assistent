package ru.studentai.core.common.validation

/**
 * Валидатор пароля.
 *
 * Реализует requirements из ТЗ §4.1.5 (безопасность аккаунта) — все правила настраиваемы
 * через параметры конструктора, что даёт возможность гибко поменять политику без
 * модификации классов-потребителей (Open/Closed).
 *
 * Дефолтная политика:
 *  • от 8 до 64 символов;
 *  • как минимум одна цифра;
 *  • как минимум одна строчная буква;
 *  • как минимум одна заглавная буква;
 *  • спецсимвол — опционально (по умолчанию выключен).
 *
 * @param field           имя поля для [ValidationError.field] (по умолчанию "password")
 * @param minLength       минимальная длина (>= 1)
 * @param maxLength       максимальная длина (по умолчанию 64 — защита от DoS)
 * @param requireDigit    требовать цифру
 * @param requireLower    требовать строчную букву
 * @param requireUpper    требовать заглавную букву
 * @param requireSpecial  требовать спецсимвол
 * @param specialCharset  набор символов, считающихся «спецсимволами»
 */
public class PasswordValidator(
    private val field: String = DEFAULT_FIELD,
    private val minLength: Int = DEFAULT_MIN_LENGTH,
    private val maxLength: Int = DEFAULT_MAX_LENGTH,
    private val requireDigit: Boolean = true,
    private val requireLower: Boolean = true,
    private val requireUpper: Boolean = true,
    private val requireSpecial: Boolean = false,
    private val specialCharset: String = DEFAULT_SPECIAL_CHARS,
) : Validator<String> {

    init {
        require(minLength >= 1) { "minLength must be >= 1, got $minLength" }
        require(maxLength >= minLength) { "maxLength ($maxLength) must be >= minLength ($minLength)" }
    }

    override fun validate(value: String): ValidationResult {
        val errors = buildList {
            if (value.isEmpty()) {
                add(ValidationError(field, RequiredFieldValidator.CODE, "Password is required"))
                return@buildList
            }
            if (value.length < minLength) {
                add(ValidationError(field, CODE_TOO_SHORT,
                    "Password must be at least $minLength characters"))
            }
            if (value.length > maxLength) {
                add(ValidationError(field, CODE_TOO_LONG,
                    "Password must be at most $maxLength characters"))
            }
            if (requireDigit && value.none { it.isDigit() }) {
                add(ValidationError(field, CODE_NO_DIGIT, "Password must contain a digit"))
            }
            if (requireLower && value.none { it.isLowerCase() }) {
                add(ValidationError(field, CODE_NO_LOWER, "Password must contain a lowercase letter"))
            }
            if (requireUpper && value.none { it.isUpperCase() }) {
                add(ValidationError(field, CODE_NO_UPPER, "Password must contain an uppercase letter"))
            }
            if (requireSpecial && value.none { specialCharset.contains(it) }) {
                add(ValidationError(field, CODE_NO_SPECIAL,
                    "Password must contain a special character"))
            }
            if (value.any { it.isWhitespace() }) {
                add(ValidationError(field, CODE_WHITESPACE, "Password must not contain whitespace"))
            }
        }
        return if (errors.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(errors)
    }

    public companion object {
        public const val DEFAULT_FIELD: String = "password"
        public const val DEFAULT_MIN_LENGTH: Int = 8
        public const val DEFAULT_MAX_LENGTH: Int = 64
        public const val DEFAULT_SPECIAL_CHARS: String = "!@#\$%^&*()_+-=[]{}|;:,.<>?/~`"

        public const val CODE_TOO_SHORT: String = "too_short"
        public const val CODE_TOO_LONG: String = "too_long"
        public const val CODE_NO_DIGIT: String = "no_digit"
        public const val CODE_NO_LOWER: String = "no_lower"
        public const val CODE_NO_UPPER: String = "no_upper"
        public const val CODE_NO_SPECIAL: String = "no_special"
        public const val CODE_WHITESPACE: String = "whitespace"
    }
}
