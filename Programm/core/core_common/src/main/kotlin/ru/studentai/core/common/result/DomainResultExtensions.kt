package ru.studentai.core.common.result

import ru.studentai.core.common.error.AppException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/** Трансформирует успешное значение. Failure пробрасывается без изменений. */
public inline fun <T, R> DomainResult<T>.map(transform: (T) -> R): DomainResult<R> {
    return when (this) {
        is DomainResult.Success -> DomainResult.Success(transform(value))
        is DomainResult.Failure -> this
    }
}

/** Цепочка операций: трансформирует значение в новый `DomainResult`. */
public inline fun <T, R> DomainResult<T>.flatMap(transform: (T) -> DomainResult<R>): DomainResult<R> {
    return when (this) {
        is DomainResult.Success -> transform(value)
        is DomainResult.Failure -> this
    }
}

/** Трансформирует ошибку. Success пробрасывается без изменений. */
public inline fun <T> DomainResult<T>.mapError(transform: (AppException) -> AppException): DomainResult<T> {
    return when (this) {
        is DomainResult.Success -> this
        is DomainResult.Failure -> DomainResult.Failure(transform(error))
    }
}

/**
 * Извлекает значение либо вычисляет fallback из ошибки.
 * Удобно для default-значений.
 */
@OptIn(ExperimentalContracts::class)
public inline fun <T> DomainResult<T>.getOrElse(fallback: (AppException) -> T): T {
    contract { callsInPlace(fallback, InvocationKind.AT_MOST_ONCE) }
    return when (this) {
        is DomainResult.Success -> value
        is DomainResult.Failure -> fallback(error)
    }
}

/** Возвращает значение или `null`, если результат — Failure. */
public fun <T> DomainResult<T>.getOrNull(): T? =
    (this as? DomainResult.Success<T>)?.value

/** Возвращает ошибку или `null`, если результат — Success. */
public fun DomainResult<*>.errorOrNull(): AppException? =
    (this as? DomainResult.Failure)?.error

/** Бросает [AppException], если результат — Failure. Используется только в тестах/edge-cases. */
public fun <T> DomainResult<T>.getOrThrow(): T = when (this) {
    is DomainResult.Success -> value
    is DomainResult.Failure -> throw error
}

/**
 * Свёртка по обоим веткам. Полезна для UI-рендеринга:
 * `result.fold(onSuccess = { ... }, onFailure = { ... })`.
 */
@OptIn(ExperimentalContracts::class)
public inline fun <T, R> DomainResult<T>.fold(
    onSuccess: (T) -> R,
    onFailure: (AppException) -> R,
): R {
    contract {
        callsInPlace(onSuccess, InvocationKind.AT_MOST_ONCE)
        callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
    }
    return when (this) {
        is DomainResult.Success -> onSuccess(value)
        is DomainResult.Failure -> onFailure(error)
    }
}

/** Сайд-эффект при успехе. Возвращает `this` для цепочек. */
@OptIn(ExperimentalContracts::class)
public inline fun <T> DomainResult<T>.onSuccess(action: (T) -> Unit): DomainResult<T> {
    contract { callsInPlace(action, InvocationKind.AT_MOST_ONCE) }
    if (this is DomainResult.Success) action(value)
    return this
}

/** Сайд-эффект при ошибке. Возвращает `this` для цепочек. */
@OptIn(ExperimentalContracts::class)
public inline fun <T> DomainResult<T>.onFailure(action: (AppException) -> Unit): DomainResult<T> {
    contract { callsInPlace(action, InvocationKind.AT_MOST_ONCE) }
    if (this is DomainResult.Failure) action(error)
    return this
}

/** Алиас [onFailure] — иногда читается приятнее. */
public inline fun <T> DomainResult<T>.onError(action: (AppException) -> Unit): DomainResult<T> =
    onFailure(action)

/** Обёртка значения в Success. */
public fun <T> T.asSuccess(): DomainResult<T> = DomainResult.Success(this)

/** Обёртка ошибки в Failure. */
public fun AppException.asFailure(): DomainResult<Nothing> = DomainResult.Failure(this)

/**
 * Рекавери от ошибки. Если результат Failure — пытается восстановить значение через [recovery],
 * иначе пробрасывает Success без изменений.
 */
public inline fun <T> DomainResult<T>.recover(recovery: (AppException) -> T): DomainResult<T> = when (this) {
    is DomainResult.Success -> this
    is DomainResult.Failure -> DomainResult.Success(recovery(error))
}

/** Аналог [recover], но рекавери может вернуть новый [DomainResult]. */
public inline fun <T> DomainResult<T>.recoverWith(
    recovery: (AppException) -> DomainResult<T>,
): DomainResult<T> = when (this) {
    is DomainResult.Success -> this
    is DomainResult.Failure -> recovery(error)
}

/** Если оба Success — комбинирует значения; иначе возвращает первую встреченную ошибку. */
public inline fun <A, B, R> DomainResult<A>.combine(
    other: DomainResult<B>,
    combiner: (A, B) -> R,
): DomainResult<R> = when (this) {
    is DomainResult.Failure -> this
    is DomainResult.Success -> when (other) {
        is DomainResult.Failure -> other
        is DomainResult.Success -> DomainResult.Success(combiner(value, other.value))
    }
}
