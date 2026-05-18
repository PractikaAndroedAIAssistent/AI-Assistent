package ru.studentai.core.common.validation

/**
 * Композиция произвольного числа валидаторов с агрегацией ошибок (без short-circuit).
 * Аналог `v1 + v2 + v3`, но удобнее, когда правила хочется хранить списком или строить динамически.
 */
public class CompositeValidator<T>(
    private val rules: List<Validator<T>>,
) : Validator<T> {

    public constructor(vararg rules: Validator<T>) : this(rules.toList())

    override fun validate(value: T): ValidationResult =
        rules.map { it.validate(value) }.merge()
}

/** Builder-style фабрика для удобного DSL. */
public fun <T> validators(vararg rules: Validator<T>): Validator<T> = CompositeValidator(*rules)
