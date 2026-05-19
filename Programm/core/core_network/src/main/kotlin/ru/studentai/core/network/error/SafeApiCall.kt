package ru.studentai.core.network.error

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext
import ru.studentai.core.common.dispatchers.DispatcherProvider
import ru.studentai.core.common.result.DomainResult
import ru.studentai.core.common.result.safeCallMapping

/**
 * Универсальная обёртка для вызова Retrofit-API.
 *
 * Объединяет:
 *  • [DispatcherProvider.io] — гарантирует, что вызов идёт на IO-пуле;
 *  • [HttpErrorMapper] — превращает HTTP/сетевые исключения в [AppException];
 *  • [safeCallMapping] — формирует [DomainResult].
 *
 * Использование в репозитории:
 * ```
 * suspend fun login(req: LoginRequest): DomainResult<UserDto> =
 *     safeApiCall { api.login(req) }
 * ```
 */
public class SafeApiCall @javax.inject.Inject constructor(
    private val dispatchers: DispatcherProvider,
    private val errorMapper: HttpErrorMapper,
) {

    public suspend operator fun <T> invoke(block: suspend () -> T): DomainResult<T> {
        return try {
            withContext(dispatchers.io) {
                safeCallMapping(
                    mapper = errorMapper::map,
                    block = block,
                )
            }
        } catch (ce: CancellationException) {
            throw ce
        }
    }
}
