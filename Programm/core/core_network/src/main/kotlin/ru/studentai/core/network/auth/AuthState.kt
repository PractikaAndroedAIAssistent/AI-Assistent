package ru.studentai.core.network.auth

/**
 * Состояние сессии пользователя.
 *
 * Эмитится через [AuthTokenStorage.authState] и наблюдается app/feature-слоями
 * для реакции на logout / истечение токена.
 */
public sealed interface AuthState {
    public data object Unauthenticated : AuthState
    public data object Authenticated : AuthState
}
