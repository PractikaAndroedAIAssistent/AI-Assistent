package ru.studentai.core.common.validation

/**
 * Результат валидации. Sealed для exhaustive when в UI.
 */
public sealed interface ValidationResult {

    public data object Valid : ValidationResult

    public data class Invalid(val errors: List<ValidationError>) : ValidationResult {
        init {
            require(errors.isNotEmpty()) { "Invalid must contain at least one error" }
        }
    }

    public companion object {
        public fun invalid(error: ValidationError): Invalid = Invalid(listOf(error))
        public fun invalid(field: String, code: String, message: String): Invalid =
            Invalid(listOf(ValidationError(field, code, message)))
    }
}

/** `true`, если результат — Valid. */
public val ValidationResult.isValid: Boolean get() = this is ValidationResult.Valid

/** `true`, если результат — Invalid. */
public val ValidationResult.isInvalid: Boolean get() = this is ValidationResult.Invalid

/** Список ошибок (пустой для Valid). */
public val ValidationResult.errors: List<ValidationError>
    get() = (this as? ValidationResult.Invalid)?.errors.orEmpty()

/**
 * Объединение результатов: все Valid → Valid, иначе Invalid с агрегированными ошибками.
 * Реализует моноидальную композицию: `a + b + c` собирает все ошибки разом, не закорачиваясь.
 */
public operator fun ValidationResult.plus(other: ValidationResult): ValidationResult {
    val all = this.errors + other.errors
    return if (all.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(all)
}

/** Свёртка списка результатов в один. */
public fun List<ValidationResult>.merge(): ValidationResult =
    fold(ValidationResult.Valid as ValidationResult) { acc, r -> acc + r }
