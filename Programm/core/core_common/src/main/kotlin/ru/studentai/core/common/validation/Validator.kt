package ru.studentai.core.common.validation

/**
 * Чистая функция валидации. SAM-интерфейс — позволяет писать
 * `Validator<String> { value -> if (...) Valid else Invalid(...) }`.
 */
public fun interface Validator<in T> {
    public fun validate(value: T): ValidationResult
}

/**
 * Композиция валидаторов: оба применяются, ошибки агрегируются.
 * Не закорачивается на первой ошибке — пользователь сразу видит все проблемы поля.
 */
public operator fun <T> Validator<T>.plus(other: Validator<T>): Validator<T> =
    Validator { value -> this.validate(value) + other.validate(value) }

/**
 * Композиция со «short-circuit»: если первый вернул Invalid, второй не вызывается.
 * Удобно, когда последующие правила не имеют смысла без выполнения предыдущих
 * (например: «не пусто» → «корректный email»).
 */
public infix fun <T> Validator<T>.then(next: Validator<T>): Validator<T> =
    Validator { value ->
        val first = this.validate(value)
        if (first is ValidationResult.Invalid) first else next.validate(value)
    }

/** Применить валидатор к списку результатов. */
public fun <T> Validator<T>.validateAll(values: Iterable<T>): ValidationResult =
    values.map(::validate).merge()
