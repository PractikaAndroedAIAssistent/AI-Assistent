package ru.studentai.core.common.result

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import ru.studentai.core.common.error.AppException
import ru.studentai.core.common.error.UnknownException

/**
 * Превращает любой `Flow<T>` в `Flow<DomainResult<T>>`:
 *  • каждое значение оборачивается в [DomainResult.Success];
 *  • [AppException] и любые `Throwable` мапятся в [DomainResult.Failure];
 *  • [CancellationException] пропускается выше для корректной structured concurrency.
 */
public fun <T> Flow<T>.asDomainResult(): Flow<DomainResult<T>> =
    map<T, DomainResult<T>> { DomainResult.Success(it) }
        .catch { throwable ->
            if (throwable is CancellationException) throw throwable
            val error = throwable as? AppException
                ?: UnknownException(throwable.message, throwable)
            emit(DomainResult.Failure(error))
        }

/**
 * Тот же [asDomainResult], но с явным первичным эмитом — полезно для UI-состояний
 * (показать loading до прихода первого значения).
 *
 * Используется так:
 * ```
 * repository.observe()
 *     .asDomainResultWithLoading { uiState.update { it.copy(loading = true) } }
 *     .onEach { result -> ... }
 * ```
 */
public fun <T> Flow<T>.asDomainResultWithLoading(
    onStart: suspend () -> Unit,
): Flow<DomainResult<T>> =
    asDomainResult().onStart { onStart() }

/**
 * Преобразует одиночное значение в Flow<DomainResult<T>> с единственным элементом.
 */
public fun <T> DomainResult<T>.asFlow(): Flow<DomainResult<T>> = flow { emit(this@asFlow) }
