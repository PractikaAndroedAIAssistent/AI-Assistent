package ru.studentai.core.common.error

/**
 * Ошибки авторизации и сессии.
 * См. ТЗ §4.1.5, §4.1.7 — пароли никогда не хранятся открыто,
 * токены лежат в Android Keystore, доступ к данным разграничен по ролям.
 */
public sealed class AuthException(
    message: String? = null,
    cause: Throwable? = null,
) : AppException(message, cause) {

    /** Неверная пара login/password. */
    public class InvalidCredentials(cause: Throwable? = null) :
        AuthException("Invalid credentials", cause)

    /** Токен истёк — нужно обновить через refresh. */
    public class TokenExpired(cause: Throwable? = null) :
        AuthException("Auth token expired", cause)

    /** Refresh-токен невалиден — требуется повторный логин. */
    public class RefreshFailed(cause: Throwable? = null) :
        AuthException("Token refresh failed", cause)

    /** Пользователь не авторизован (нет токена / 401). */
    public class Unauthorized(cause: Throwable? = null) :
        AuthException("Unauthorized", cause)

    /** Текущая роль не имеет прав на действие (403). */
    public class Forbidden(
        public val requiredRole: String? = null,
        cause: Throwable? = null,
    ) : AuthException(
        requiredRole?.let { "Forbidden, role '$it' required" } ?: "Forbidden",
        cause,
    )

    /** Email уже занят при регистрации. */
    public class EmailAlreadyTaken(cause: Throwable? = null) :
        AuthException("Email is already registered", cause)

    /** Аккаунт заблокирован / отключён. */
    public class AccountDisabled(cause: Throwable? = null) :
        AuthException("Account is disabled", cause)
}
