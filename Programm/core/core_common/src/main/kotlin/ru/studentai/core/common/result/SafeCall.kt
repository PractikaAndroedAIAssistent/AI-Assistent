package ru.studentai.core.common.result

import kotlinx.coroutines.CancellationException
import ru.studentai.core.common.error.AppException
import ru.studentai.core.common.error.UnknownException

/**
 * Универсальный безопасный вызов с гарантированным маппингом исключений в [DomainResult].
 *
 * Правила:
 *  • [CancellationException] **никогда не глотается** — это критично для structured concurrency:
 *    отмена должна доходить до родительской корутины;
 *  • [AppException] оборачивается «как есть» в [DomainResult.Failure];
 *  • любой другой `Throwable` оборачивается в [UnknownException] — наружу не должны утекать
 *    «сырые» исключения, которые presentation-слой не знает как обработать.
 *
 * Использование:
 * ```
 * suspend fun fetch(): DomainResult<User> = safeCall { api.getUser() }
 * ```
 */
public suspend inline fun <T> safeCall(crossinline block: suspend () -> T): DomainResult<T> {
    return try {
        DomainResult.Success(block())
    } catch (ce: CancellationException) {
        throw ce
    } catch (ae: AppException) {
        DomainResult.Failure(ae)
    } catch (t: Throwable) {
        DomainResult.Failure(UnknownException(t.message, t))
    }
}

/**
 * Версия [safeCall] с явным маппером исключений.
 * Полезна, когда конкретный слой знает специфику ошибок source-источника
 * (например, OkHttp → NetworkException).
 */
public suspend inline fun <T> safeCallMapping(
    crossinline mapper: (Throwable) -> AppException,
    crossinline block: suspend () -> T,
): DomainResult<T> {
    return try {
        DomainResult.Success(block())
    } catch (ce: CancellationException) {
        throw ce
    } catch (ae: AppException) {
        DomainResult.Failure(ae)
    } catch (t: Throwable) {
        DomainResult.Failure(mapper(t))
    }
}
