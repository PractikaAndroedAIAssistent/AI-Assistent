package ru.studentai.core.network.auth

import kotlinx.coroutines.flow.Flow

/**
 * Безопасное хранилище JWT-токенов сессии.
 *
 * ТЗ §4.1.5: пароли не хранятся в открытом виде; токены — в Android Keystore
 * либо в EncryptedSharedPreferences. Реализация по умолчанию —
 * [EncryptedAuthTokenStorage].
 *
 * Контракт:
 *  • все методы suspend — реальная I/O выполняется на IO-dispatcher'е;
 *  • [authState] эмитит при изменении (saveTokens / clear).
 */
public interface AuthTokenStorage {

    public val authState: Flow<AuthState>

    public suspend fun getAccessToken(): String?

    public suspend fun getRefreshToken(): String?

    public suspend fun saveTokens(accessToken: String, refreshToken: String)

    public suspend fun clear()
}
