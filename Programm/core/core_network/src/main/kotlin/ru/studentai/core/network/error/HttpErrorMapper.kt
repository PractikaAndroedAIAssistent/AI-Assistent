package ru.studentai.core.network.error

import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import ru.studentai.core.common.error.AppException
import ru.studentai.core.common.error.AuthException
import ru.studentai.core.common.error.NetworkException

/**
 * Маппер технических исключений HTTP-стека в доменный [AppException].
 *
 * Контракт:
 *  • [map] никогда не возвращает «сырой» Throwable выше — только [AppException];
 *  • [CancellationException] пробрасывается без оборачивания (structured concurrency);
 *  • mapper открыт для наследования (`open`) — конкретный API может уточнить семантику
 *    отдельных HTTP-кодов (например, 422 как ValidationException).
 */
@Singleton
public open class HttpErrorMapper @Inject constructor() {

    /** Главная точка входа. */
    public open fun map(throwable: Throwable): AppException {
        if (throwable is CancellationException) throw throwable
        if (throwable is AppException) return throwable
        return when (throwable) {
            is HttpException -> mapHttpException(throwable)
            is SerializationException -> NetworkException.Serialization(throwable.message, throwable)
            is SocketTimeoutException -> NetworkException.Timeout(throwable)
            is UnknownHostException -> NetworkException.NoConnection(throwable)
            is ConnectException -> NetworkException.NoConnection(throwable)
            is IOException -> NetworkException.NoConnection(throwable)
            else -> NetworkException.Server(
                code = -1,
                message = throwable.message ?: "Unknown network error",
                cause = throwable,
            )
        }
    }

    /** Маппит [HttpException] по коду ответа. */
    protected open fun mapHttpException(exception: HttpException): AppException {
        val code = exception.code()
        val body: String? = try {
            exception.response()?.errorBody()?.string()
        } catch (ioe: IOException) {
            null
        }
        return when (code) {
            in HTTP_CLIENT_ERROR_START until HTTP_SERVER_ERROR_START -> mapClientError(code, body, exception)
            in HTTP_SERVER_ERROR_START until HTTP_INVALID_END -> NetworkException.Server(code, body, exception)
            else -> NetworkException.Http(code, body, exception)
        }
    }

    private fun mapClientError(code: Int, body: String?, cause: HttpException): AppException = when (code) {
        HTTP_UNAUTHORIZED -> AuthException.Unauthorized(cause)
        HTTP_FORBIDDEN -> AuthException.Forbidden(cause = cause)
        else -> NetworkException.Http(code, body, cause)
    }

    public companion object {
        public const val HTTP_UNAUTHORIZED: Int = 401
        public const val HTTP_FORBIDDEN: Int = 403
        public const val HTTP_CLIENT_ERROR_START: Int = 400
        public const val HTTP_SERVER_ERROR_START: Int = 500
        public const val HTTP_INVALID_END: Int = 600
    }
}
