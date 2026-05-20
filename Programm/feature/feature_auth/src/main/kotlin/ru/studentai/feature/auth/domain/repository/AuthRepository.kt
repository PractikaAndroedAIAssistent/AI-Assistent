package ru.studentai.feature.auth.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.studentai.core.common.result.DomainResult
import ru.studentai.core.network.auth.AuthState
import ru.studentai.feature.auth.domain.model.AuthCredentials
import ru.studentai.feature.auth.domain.model.RegistrationData
import ru.studentai.feature.auth.domain.model.User
import ru.studentai.feature.auth.domain.model.UserProfile

/**
 * Domain-контракт аутентификации.
 *
 * Implementация (`AuthRepositoryImpl`) находится в data-слое и оркестрирует:
 *   AuthApi (Retrofit) + AuthTokenStorage (core_network).
 *
 * Все методы возвращают [DomainResult] — failure ловится в presentation-слое и мапится
 * через `ErrorMessageResolver` (core_ui).
 */
public interface AuthRepository {

    /** Поток состояния сессии (Authenticated / Unauthenticated), наблюдается app-уровнем. */
    public val authState: Flow<AuthState>

    /**
     * Вход. При успехе:
     *  • сохраняет JWT в EncryptedSharedPreferences;
     *  • эмитит Authenticated в authState.
     */
    public suspend fun login(credentials: AuthCredentials): DomainResult<User>

    /**
     * Регистрация. При успехе:
     *  • получает первичные токены и сохраняет в storage;
     *  • эмитит Authenticated.
     */
    public suspend fun register(data: RegistrationData): DomainResult<User>

    /**
     * Выход. При успехе:
     *  • вызывает серверный `/auth/logout` (аннулирование токена сессии — ТЗ §4.2.1);
     *  • очищает локальные токены даже при сетевой ошибке (best-effort).
     */
    public suspend fun logout(): DomainResult<Unit>

    /** Возвращает текущий полный профиль с сервера. */
    public suspend fun currentProfile(): DomainResult<UserProfile>

    /** Обновляет полный профиль на сервере. */
    public suspend fun updateProfile(profile: UserProfile): DomainResult<UserProfile>
}
