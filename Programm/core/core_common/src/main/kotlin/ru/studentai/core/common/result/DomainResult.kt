package ru.studentai.core.common.result

import ru.studentai.core.common.error.AppException

/**
 * Контейнер результата операции домена/UseCase'а/Repository.
 *
 * Используется ВЕЗДЕ выше data-слоя вместо «сырых» Throwable и `kotlin.Result`.
 * Причины:
 *  • `kotlin.Result` не предназначен для возврата из публичного API;
 *  • выгоднее иметь sealed-тип с понятным `Success/Failure` для exhaustive when;
 *  • ошибка ограничена иерархией [AppException] — никаких неожиданных RuntimeException.
 *
 * Семантика:
 *  • [Success] всегда содержит значение (для Unit-операций — `Success(Unit)`);
 *  • [Failure] всегда содержит [AppException] (а не `Throwable`).
 */
public sealed interface DomainResult<out T> {

    public data class Success<T>(public val value: T) : DomainResult<T>

    public data class Failure(public val error: AppException) : DomainResult<Nothing>

    public companion object {
        public fun <T> success(value: T): DomainResult<T> = Success(value)
        public fun failure(error: AppException): DomainResult<Nothing> = Failure(error)
    }
}

/** `true`, если результат — успех. */
public val DomainResult<*>.isSuccess: Boolean
    get() = this is DomainResult.Success

/** `true`, если результат — ошибка. */
public val DomainResult<*>.isFailure: Boolean
    get() = this is DomainResult.Failure
